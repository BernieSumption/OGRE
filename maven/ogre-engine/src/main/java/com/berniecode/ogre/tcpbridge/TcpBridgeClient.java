package com.berniecode.ogre.tcpbridge;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.MessageClientAdapter;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.wireformat.OgreWireFormatV1Serialiser;

//TODO detect NoSuchThingException error messages from server and throw exceptions
//TODO now that the server is single object graphed, this client should detect requests for invalid graphs
public class TcpBridgeClient extends InitialisingBean implements DownloadClientAdapter, MessageClientAdapter {

	private InetAddress host;
	private Integer port;
	private OgreWireFormatV1Serialiser serialiser = new OgreWireFormatV1Serialiser();

	/**
	 * Create a {@link TcpBridgeClient} with the specified host and port, and initialise it
	 */
	public TcpBridgeClient(InetAddress host, int port) {
		this.host = host;
		this.port = port;
		initialise();
	}

	/**
	 * Create a {@link TcpBridgeClient} with the specified host and port, and initialise it
	 */
	public TcpBridgeClient(String host, int port) throws UnknownHostException {
		this(InetAddress.getByName(host), port);
	}

	/**
	 * Create an uninitialised {@link TcpBridgeClient}
	 */
	public TcpBridgeClient() {
	}

	@Override
	protected void doInitialise() {
		requireNotNull(host, "host");
		requireNotNull(port, "port");
	}

	public void setPort(int port) {
		requireInitialised(false, "setPort()");
		this.port = port;
	}

	public void setHost(InetAddress host) {
		requireInitialised(false, "setHost()");
		this.host = host;
	}

	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		try {
			Socket socket = null;
			try {
				socket = new Socket(host, port);
				socket.getOutputStream().write(RequestType.CODE_TYPE_DOMAIN);
				return serialiser.deserialiseTypeDomain(socket.getInputStream());
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
		} catch (IOException e) {
			throw new OgreException("Could not load type domain", e);
		}
	}

	@Override
	public GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
		// TODO split readEnvelopedBytes out of OgreWireFormatSerialiser, it will help refactor
		// these methods
		try {
			Socket socket = null;
			try {
				socket = new Socket(host, port);
				socket.getOutputStream().write(RequestType.CODE_OBJECT_GRAPH);
				return serialiser.deserialiseGraphUpdate(socket.getInputStream(), typeDomain);
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
		} catch (IOException e) {
			throw new OgreException("Could not load type domain", e);
		}
	}

	@Override
	public void subscribeToGraphUpdates(TypeDomain typeDomain, String objectGraphId, GraphUpdateListener listener) {
		Thread t = new SubscribeThread(typeDomain, listener);
		t.start();
	}
	


	private class SubscribeThread extends Thread {

		private final TypeDomain typeDomain;
		private final GraphUpdateListener listener;

		public SubscribeThread(TypeDomain typeDomain, GraphUpdateListener listener) {
			this.typeDomain = typeDomain;
			this.listener = listener;
		}
		
		@Override
		public void run() {
			try {
				Socket socket = null;
				try {
					socket = new Socket(host, port);
					InputStream inputStream = socket.getInputStream();
					socket.getOutputStream().write(RequestType.CODE_SUBSCRIBE);
					while (true) {
						GraphUpdate update = serialiser.deserialiseGraphUpdate(inputStream, typeDomain);
						if (OgreLog.isDebugEnabled()) {
							OgreLog.debug("TcpBridgeClient: received new update message: " + EDRDescriber.describeGraphUpdate(update));
						}
						listener.acceptGraphUpdate(update);
					}
				} finally {
					if (socket != null) {
						socket.close();
					}
				}
			} catch (IOException e) {
				throw new OgreException("Could not load type domain", e);
			}
		}
		
	}

}
