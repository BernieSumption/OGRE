package com.berniecode.ogre.tcpbridge;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.server.MessageServerAdapter;
import com.berniecode.ogre.server.ServerEngine;
import com.berniecode.ogre.wireformat.OgreWireFormatV1Serialiser;

/**
 * A TCP socket serverEngine that exposes type domains and object graphs over a TCP connection.
 * 
 * <p>
 * This implementation uses raw calls to the java.nio package. I wrote it as an exercise in learning
 * the package, but since NIO is notoriously hard to get right, I expect it contains bugs. I don't
 * consider it suitable for production without extensive testing.
 * 
 * TODO I should rewrite this using Netty (or QuickServer / Grizzly)
 * 
 * <p>
 * The protocol is super simple. The server listens for connections on a configurable TCP port. When
 * a client connects, the serverEngine reads a single line of input from the client then sends a
 * response depending on the requestBuilder line:
 * 
 * <ul>
 * <li>If the line is "loadTypeDomain[TAB]typeDomainId" then the server sends the requested type
 * domain as a TypeDomainMessage then closes the connection.
 * <li>If the line is "loadObjectGraph[TAB]typeDomainId[TAB]objectGraphId" then the server sends the
 * requested object graph as a complete-style GraphUpdateMessage then closes the connection.
 * <li>If the line is "subscribeToGraphUpdates[TAB]typeDomainId[TAB]objectGraphId" then the server
 * will keep the connection open, sending a diff-style GraphUpdateMessage every time the specified
 * object graph changes. The connection will remain open until it is closed by the client or the
 * server quits
 * </ul>
 * 
 * <p>
 * Responses are serialised in the OGRE Wire Format.
 * 
 * @author Bernie Sumption
 */
public class TcpBridgeServer extends InitialisingBean implements MessageServerAdapter {

	private static final Charset CHARACTER_SET = Charset.forName("UTF8");
	//
	// This server uses Java 1.4 nonblocking IO. The implementation is a pretty trivial NIO
	// networking example. In stead of line-by-line comments, read this excellent tutorial:
	// http://rox-xmlrpc.sourceforge.net/niotut/
	//

	//
	// CONFIGURATION
	//

	private EDRSerialiser serialiser = new OgreWireFormatV1Serialiser();
	private ServerEngine serverEngine;
	private InetAddress hostAddress;
	private Integer port;

	public TcpBridgeServer() {
		try {
			hostAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
		}
	}

	/**
	 * Set the IP hostAddress to listen for new connections on.
	 * 
	 * If no value is provided for this property, the loopback DNS name ("localhost") will be used
	 */
	public void setHost(InetAddress host) {
		requireInitialised(false, "setHost()");
		this.hostAddress = host;
	}

	/**
	 * Set the {@link EDRSerialiser} to encode responses. If no value is provided,
	 * {@link OgreWireFormatV1Serialiser} will be used.
	 */
	public void setSerialiser(EDRSerialiser serialiser) {
		requireInitialised(false, "setSerialiser()");
		this.serialiser = serialiser;
	}

	/**
	 * Set the TCP port to listen for new connections on.
	 */
	public void setPort(int port) {
		requireInitialised(false, "setPort()");
		this.port = port;
	}

	/**
	 * Set the server engine to be exposed through this download adapter.
	 */
	public void setServerEngine(ServerEngine server) {
		requireInitialised(false, "setServerEngine()");
		this.serverEngine = server;
	}

	/**
	 * Stop the server. The server immediately stops accepting incoming connections, and stops
	 * writing data to outgoing new connections. This may result in the transmission of truncated,
	 * malformed responses to clients.
	 * 
	 * <p>
	 * This method blocks until the server has released the incoming TCP port, or until the current
	 * thread is interrupted, whichever comes first.
	 */
	public void quit() {
		run = false;
		selector.wakeup();
		try {
			serverThread.join();
		} catch (InterruptedException e) {
		}
	}

	//
	// INITIALISATION
	//

	private ServerSocketChannel serverChannel;
	private Selector selector;
	private Map<SocketChannel, Conversation> conversations = new HashMap<SocketChannel, Conversation>();

	private Thread serverThread;
	private boolean run = true;

	private ByteBuffer readBuffer = ByteBuffer.allocate(4096);

	private Set<SocketChannel> writeRequests = new HashSet<SocketChannel>();

	@Override
	protected void doInitialise() {
		requireNotNull(hostAddress, "hostAddress");
		requireNotNull(port, "port");
		requireNotNull(serverEngine, "serverEngine");
		requireNotNull(serialiser, "serialiser");

		try {
			selector = SelectorProvider.provider().openSelector();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(hostAddress, port));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

			serverThread = new SelectorThread();
			serverThread.start();
		} catch (IOException e) {
			throw new OgreException("Failed to start TcpBridgeServer", e);
		}
	}

	/*
	 * SELECTION LOOP
	 * 
	 * SelectorThread.run() and the methods called from there are the selection loop. This single
	 * threaded server handles all accepting of connections, reading data and writing responses.
	 * This means that everything in this loop must be super fast, avoiding any blocking calls or
	 * CPU-intensive tasks.
	 */

	private final class SelectorThread extends Thread {
		public SelectorThread() {
			super("SelectorThread");
		}

		@Override
		public void run() {
			while (run) {

				for (SocketChannel writeRequest : writeRequests) {
					SelectionKey key = writeRequest.keyFor(selector);
					key.interestOps(SelectionKey.OP_WRITE);
				}
				writeRequests.clear();

				try {
					selector.select();
				} catch (IOException e) {
					OgreLog.error("Error in bridge server: " + e.getMessage());
				}

				Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!run || !key.isValid()) {
						continue;
					}

					if (key.isAcceptable()) {
						acceptConnection(); // no need to pass key - only the serverChannel can
											// accept connections
					} else if (key.isReadable()) {
						readFromSocket(key);
					} else if (key.isWritable()) {
						writeToSocket(key);
					}
				}
			}
			for (SelectionKey key : selector.keys()) {
				closeConnection(key);
			}
		}
	}

	private void acceptConnection() {

		SocketChannel socketChannel;
		try {
			socketChannel = serverChannel.accept();
			socketChannel.configureBlocking(false);
			socketChannel.register(this.selector, SelectionKey.OP_READ);
			synchronized (conversations) {
				conversations.put(socketChannel, new Conversation());
			}
		} catch (IOException e) {
			OgreLog.error("Exception while accepting a connection: " + e.getMessage());
			return;
		}
		System.err.println("accepted " + socketChannel);
	}

	private void readFromSocket(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		readBuffer.clear();
		int numRead;
		try {
			numRead = socketChannel.read(readBuffer);
		} catch (IOException e) {
			closeConnection(key); // connection forcibly closed by client
			return;
		}

		if (numRead == -1) {
			closeConnection(key); // connection politely closed by client
			return;
		}

		String read = new String(readBuffer.array(), 0, numRead, CHARACTER_SET);

		Conversation conversation;
		synchronized (conversations) {
			conversation = conversations.get(socketChannel);
		}
		if (conversation != null) {
			boolean isComplete = conversation.acceptData(read);
			if (isComplete) {
				handleRequestComplete(key, socketChannel, conversation);
			}
		}
	}

	private void handleRequestComplete(SelectionKey key, SocketChannel socketChannel, Conversation conversation) {
		if (conversation.isError()) {
			// TODO send error response to client
			OgreLog.error(conversation.getError());
			closeConnection(key);
		} else {
			writeRequests.add(socketChannel);
			switch (conversation.getType()) {
			case LOAD_TYPE_DOMAIN:
				try {
					// TODO cache byte array
					TypeDomain typeDomain = serverEngine.getTypeDomain(conversation.getTypeDomainId());
					byte[] response = serialiser.serialiseTypeDomain(typeDomain);
					conversation.addDataToSend(response);
				} catch (NoSuchThingException e) {
					// TODO send error response to client
					OgreLog.error(e.getMessage());
					closeConnection(key);
				}
				break;
			case LOAD_OBJECT_GRAPH:
				try {
					// TODO cache byte array
					GraphUpdate graphUpdate = serverEngine.getObjectGraph(conversation.getTypeDomainId(),
							conversation.getObjectGraphId());
					byte[] response = serialiser.serialiseGraphUpdate(graphUpdate);
					conversation.addDataToSend(response);
				} catch (NoSuchThingException e) {
					// TODO send error response to client
					OgreLog.error(e.getMessage());
					closeConnection(key);
				}
				break;
			case SUBSCRIBE_TO_GRAPH_UPDATES:
				try {
					// TODO cache byte array
					GraphUpdate graphUpdate = serverEngine.getObjectGraph(conversation.getTypeDomainId(),
							conversation.getObjectGraphId());
					byte[] response = serialiser.serialiseGraphUpdate(graphUpdate);
					conversation.addDataToSend(response);
				} catch (NoSuchThingException e) {
					// TODO send error response to client
					OgreLog.error(e.getMessage());
					closeConnection(key);
				}
				break;
			}
		}
	}

	private void writeToSocket(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		Conversation conversation;
		synchronized (conversations) {
			conversation = conversations.get(socketChannel);
		}
		if (conversation != null) {
			ByteBuffer data = conversation.getNextDataToSend();
			if (data == null) {
				if (conversation.isCloseOnComplete()) {
					closeConnection(key);
				}
			} else {
				try {
					socketChannel.write(data);
				} catch (IOException e) {
					OgreLog.error("Exception while writing to client: " + e.getMessage());
					closeConnection(key);
				}
			}
		}
	}

	private void closeConnection(SelectionKey key) {
		try {
			key.channel().close();
		} catch (IOException e) {
			OgreLog.error("Exception while closing connection " + key + ": " + e.getMessage());
		}
		key.cancel();
		synchronized (conversations) {
			conversations.remove(key.channel());
		}
	}

	@Override
	public void publishGraphUpdate(GraphUpdate update) {
		// TODO Auto-generated method stub
		
	}

}
