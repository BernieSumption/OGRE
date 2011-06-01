/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.DataSource;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.server.SerialisedDataSource;
import com.berniecode.ogre.wireformat.Envelope;

/**
 * A TCP socket server that exposes a {@link DataSource} over a TCP connection.
 * 
 * <p>
 * This implementation uses raw calls to the java.nio package. I wrote it as an exercise in learning
 * the package, but since NIO is a bit hard to get right, I expect it contains bugs. I don't
 * consider it suitable for production without extensive testing.
 * 
 * TODO I should rewrite this using Netty (or QuickServer / Grizzly)
 * 
 * <p>
 * The protocol is super simple. The server listens for connections on a configurable TCP port. When
 * a client connects, the serverEngine reads a single byte of input from the client then sends a
 * response depending on the request byte:
 * 
 * <ul>
 * <li>If the byte is 0x01 then the server sends the data source's type domain as a
 * TypeDomainMessage then closes the connection.
 * <li>If the line is 0x02 then the server sends the data source's object graph as a complete-style
 * GraphUpdateMessage then closes the connection.
 * <li>If the line is 0x03 then the server will keep the connection open, sending a diff-style
 * GraphUpdateMessage every time data source's object graph changes. The connection will remain open
 * until it is closed by the client or the server quits
 * </ul>
 * 
 * @author Bernie Sumption
 */
public class SimpleTcpTransportServer extends InitialisingBean implements SerialisedDataSource.Listener {

	//
	// This server uses Java 1.4 nonblocking IO. The implementation is a pretty trivial NIO
	// networking example. In stead of line-by-line comments, read this excellent tutorial:
	// http://rox-xmlrpc.sourceforge.net/niotut/
	//

	//
	// CONFIGURATION
	//

	private SerialisedDataSource dataSource;
	private InetAddress hostAddress;
	private Integer port;

	/**
	 * Construct an uninitialised {@link SimpleTcpTransportServer}
	 */
	public SimpleTcpTransportServer() {
		try {
			hostAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
		}
	}


	/**
	 * Construct and initialise a {@link SimpleTcpTransportServer}
	 */
	public SimpleTcpTransportServer(InetAddress hostAddress, Integer port, SerialisedDataSource dataSource) {
		this.hostAddress = hostAddress;
		this.port = port;
		this.dataSource = dataSource;
		initialise();
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
	 * Set the TCP port to listen for new connections on.
	 */
	public void setPort(int port) {
		requireInitialised(false, "setPort()");
		this.port = port;
	}

	/**
	 * Set the server engine to be exposed through this download adapter.
	 */
	public void setDataSource(SerialisedDataSource dataSource) {
		requireInitialised(false, "setDataSource()");
		this.dataSource = dataSource;
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
	private Map<SocketChannel, Response> conversations = new HashMap<SocketChannel, Response>();

	private Thread serverThread;
	private boolean run = true;

	private ByteBuffer readBuffer = ByteBuffer.allocate(256);

	private Set<SocketChannel> writeRequests = new HashSet<SocketChannel>();

	@Override
	protected void doInitialise() {
		requireNotNull(hostAddress, "hostAddress");
		requireNotNull(port, "port");
		requireNotNull(dataSource, "dataSource");

		dataSource.addSerialisedGraphUpdateListener(this);

		try {
			selector = SelectorProvider.provider().openSelector();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(hostAddress, port));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

			serverThread = new SelectorThread();
			serverThread.start();
		} catch (IOException e) {
			throw new OgreException("Failed to start SimpleTcpTransportServer", e);
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

					try {

						if (key.isAcceptable()) {
							acceptConnection();
						} else if (key.isReadable()) {
							readFromSocket(key);
						} else if (key.isWritable()) {
							writeToSocket(key);
						}

					} catch (Exception e) {
						OgreLog.error("Closing connection due to exception: " + e.getMessage());
						closeConnection(key);
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
		} catch (IOException e) {
			OgreLog.error("Exception while accepting a connection: " + e.getMessage());
			return;
		}
		OgreLog.info("SimpleTcpTransportServer: accepted " + socketChannel);
	}

	private void readFromSocket(SelectionKey key) {

		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (conversations) {
			if (conversations.containsKey(socketChannel)) {
				OgreLog.error("Reading data from socket after the time for reading has past. "
						+ "Should not be possible: " + socketChannel);
				return;
			}
		}

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

		int requestByte = readBuffer.get(0);
		Response response;

		writeRequests.add(socketChannel);
		switch (requestByte) {
		case RequestType.CODE_TYPE_DOMAIN:
			response = new Response(RequestType.TYPE_DOMAIN);
			response.addDataToSend(Envelope.wrapInEnvelope(dataSource.getTypeDomain(),
					Envelope.TYPE_DOMAIN_MESSAGE_PAYLOAD));
			break;
		case RequestType.CODE_OBJECT_GRAPH:
			response = new Response(RequestType.OBJECT_GRAPH);
			response.addDataToSend(Envelope.wrapInEnvelope(dataSource.getCurrentSnapshot(),
					Envelope.OBJECT_GRAPH_MESSAGE_PAYLOAD));
			break;
		case RequestType.CODE_SUBSCRIBE:
			response = new Response(RequestType.SUBSCRIBE);
			break;
		default:
			closeConnection(key);
			OgreLog.error("Invalid request byte: " + requestByte);
			return;
		}

		synchronized (conversations) {
			conversations.put(socketChannel, response);
		}
	}

	private void writeToSocket(SelectionKey key) {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		Response conversation;
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
	public void acceptSerialisedGraphUpdate(byte[] update) {
		update = Envelope.wrapInEnvelope(update, Envelope.OBJECT_GRAPH_MESSAGE_PAYLOAD);
		synchronized (conversations) {
			for (Response response : conversations.values()) {
				if (response.getType() == RequestType.SUBSCRIBE) {
					response.addDataToSend(update);
				}
			}
		}
	}

}

enum RequestType {
	TYPE_DOMAIN, OBJECT_GRAPH, SUBSCRIBE;

	public static final int CODE_TYPE_DOMAIN = 1;
	public static final int CODE_OBJECT_GRAPH = 2;
	public static final int CODE_SUBSCRIBE = 3;
}

class Response {

	private String error;
	private RequestType type;

	private Queue<ByteBuffer> dataToSend = new LinkedList<ByteBuffer>();

	public Response(RequestType type) {
		this.type = type;
	}

	/**
	 * Return the error message text, or null if there was no error
	 */
	public String getError() {
		return error;
	}

	/**
	 * Return the type of this request, or null if there has been an error
	 */
	public RequestType getType() {
		return type;
	}

	/**
	 * Check whether this conversation should be closed when the remaining data has finished writing
	 */
	public boolean isCloseOnComplete() {
		return type != RequestType.SUBSCRIBE;
	}

	/**
	 * Send some bytes to the client then close the connection
	 */
	public void addDataToSend(byte[] response) {
		synchronized (dataToSend) {
			dataToSend.add(ByteBuffer.wrap(response));
		}
	}

	/**
	 * Return a {@link ByteBuffer} of data to send, or null if there is no data remaining
	 */
	public ByteBuffer getNextDataToSend() {
		synchronized (dataToSend) {
			while (true) {
				if (dataToSend.size() == 0) {
					return null;
				}
				if (dataToSend.peek().hasRemaining()) {
					return dataToSend.peek();
				}
				dataToSend.poll();
			}
		}
	}

}
