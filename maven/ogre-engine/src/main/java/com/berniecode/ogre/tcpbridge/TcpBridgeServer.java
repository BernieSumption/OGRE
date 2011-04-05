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
import java.util.Iterator;
import java.util.Map;

import com.berniecode.ogre.EDRSerialiser;
import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.server.ServerEngine;
import com.berniecode.ogre.wireformat.OgreWireFormatV1Serialiser;

/**
 * A TCP socket serverEngine that exposes type domains and object graphs over a TCP connection.
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
 * enters into a loop, sending a diff-style GraphUpdateMessage for each change to the object graph,
 * as the changes occur. This loop continues until the client disconnects or the server exits.
 * </ul>
 * 
 * <p>
 * Responses are serialised in the OGRE Wire Format.
 * 
 * @author Bernie Sumption
 */
public class TcpBridgeServer extends InitialisingBean {

	private static final Charset CHARACTER_SET = Charset.forName("UTF8");
	//
	// This server uses Java 1.4 nonblocking IO. The implementation is a pretty trivial NIO
	// networking example. In stead of line-by-line comments, read this excellent tutorial:
	// http://rox-xmlrpc.sourceforge.net/niotut/
	//

	private EDRSerialiser serialiser = new OgreWireFormatV1Serialiser();
	private ServerEngine serverEngine;
	private InetAddress hostAddress;
	private Integer port;

	private ServerSocketChannel serverChannel;
	private Selector selector;
	private Map<SocketChannel, Conversation> conversations = new HashMap<SocketChannel, Conversation>();

	private Thread serverThread;
	private boolean run = true;

	private ByteBuffer readBuffer = ByteBuffer.allocate(4096);

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
	 */
	public void quit() {
		run = false;
		selector.wakeup();
	}
	
	//
	// INTERNAL MACHINERY
	//


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

	private final class SelectorThread extends Thread {
		public SelectorThread() {
			super("SelectorThread");
		}
		@Override
		public void run() {
			while (run) {
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
						acceptConnection(key);
					} else if (key.isReadable()) {
						readFromSocket(key);
					}
				}
			}
			for (SelectionKey key : selector.keys()) {
				closeConnection(key);
			}
		}
	}

	private void acceptConnection(SelectionKey key) {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

		SocketChannel socketChannel;
		try {
			socketChannel = serverChannel.accept();
			socketChannel.configureBlocking(false);
			socketChannel.register(this.selector, SelectionKey.OP_READ);
			synchronized (conversations) {
				conversations.put(socketChannel, new Conversation(key));
			}
		} catch (IOException e) {
			OgreLog.error("Exception while accepting a connection: " + e.getMessage());
			key.cancel();
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
		if (conversation != null && conversation.acceptData(read)) {
			// the request is complete, do something sensible with it
			if (conversation.isError()) {
				// TODO send error response to client
				OgreLog.error(conversation.getError());
			} else {
				switch (conversation.getType()) {
				case LOAD_TYPE_DOMAIN:
					try {
						TypeDomain typeDomain = serverEngine.getTypeDomain(conversation.getTypeDomainId());
						byte[] response = serialiser.serialiseTypeDomain(typeDomain);
						conversation.sendData("DONE, hey!\n".getBytes(), true);
					} catch (NoSuchThingException e) {
						// TODO send error response to client
						OgreLog.error(e.getMessage());
						closeConnection(key);
					}
					break;
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

}
