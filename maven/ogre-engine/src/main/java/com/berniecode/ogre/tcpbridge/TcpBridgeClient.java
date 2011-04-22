package com.berniecode.ogre.tcpbridge;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.berniecode.ogre.EDRDeserialiser;
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
import com.berniecode.ogre.wireformat.Envelope;
import com.berniecode.ogre.wireformat.OgreWireFormatDeserialiser;

/**
 * A client to match {@link TcpBridgeServer}
 * 
 * @author Bernie Sumption
 */
public class TcpBridgeClient extends InitialisingBean implements DownloadClientAdapter, MessageClientAdapter {

	private InetAddress host;
	private Integer port;
	private EDRDeserialiser deserialiser = new OgreWireFormatDeserialiser();

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

	/**
	 * The remote host to connect to
	 */
	public void setHost(InetAddress host) {
		requireInitialised(false, "setHost()");
		this.host = host;
	}

	/**
	 * The TCP port number on the remote host to connect to
	 */
	public void setPort(int port) {
		requireInitialised(false, "setPort()");
		this.port = port;
	}

	/**
	 * The derserialiser to use for incoming messages. If no value is provided, the default is
	 * {@link OgreWireFormatDeserialiser}
	 * 
	 * @param deserialiser
	 */
	public void setEDRDeserialiser(EDRDeserialiser deserialiser) {
		requireInitialised(false, "setEDRDeserialiser()");
		this.deserialiser = deserialiser;
	}

	/**
	 * Load the type domain that the {@link TcpBridgeServer} provides. Since {@link TcpBridgeServer}
	 * only exposes a single type domain, the argument is ignored
	 */
	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		byte[] message = doEnvelopedRequest(RequestType.CODE_TYPE_DOMAIN);
		return deserialiser.deserialiseTypeDomain(message);
	}


	/**
	 * Load the type object graph that the {@link TcpBridgeServer} provides. Since {@link TcpBridgeServer}
	 * only exposes a single object graph, the arguments are ignored
	 */
	@Override
	public GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
		byte[] message = doEnvelopedRequest(RequestType.CODE_OBJECT_GRAPH);
		return deserialiser.deserialiseGraphUpdate(message, typeDomain);
	}

	/**
	 * Subscribe to the graph updates that the {@link TcpBridgeServer} provides. Since {@link TcpBridgeServer}
	 * only exposes a single object graph, the argument is ignored
	 */
	@Override
	public void subscribeToGraphUpdates(TypeDomain typeDomain, String objectGraphId, GraphUpdateListener listener) {
		Thread t = new SubscribeThread(typeDomain, listener);
		t.start();
	}
	
	//
	// INTERNAL MACHINERY
	//
	
	private byte[] doEnvelopedRequest(int requestCode) {
		try {
			Socket socket = null;
			try {
				socket = new Socket(host, port);
				socket.getOutputStream().write(requestCode);
				return Envelope.readEnvelopedBytes(socket.getInputStream());
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
		} catch (IOException e) {
			throw new OgreException("Could not execute request", e);
		}
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
						byte[] message = Envelope.readEnvelopedBytes(inputStream);
						GraphUpdate update = deserialiser.deserialiseGraphUpdate(message, typeDomain);
						if (OgreLog.isDebugEnabled()) {
							OgreLog.debug("TcpBridgeClient: received new update message: "
									+ EDRDescriber.describeGraphUpdate(update));
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
