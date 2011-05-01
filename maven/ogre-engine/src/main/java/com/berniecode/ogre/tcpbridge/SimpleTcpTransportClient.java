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
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.berniecode.ogre.EDRDeserialiser;
import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.ClientTransportAdapter;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.wireformat.Envelope;
import com.berniecode.ogre.wireformat.OgreWireFormatDeserialiser;

/**
 * A client to match {@link SimpleTcpTransportServer}
 * 
 * @author Bernie Sumption
 */
public class SimpleTcpTransportClient extends InitialisingBean implements ClientTransportAdapter {

	private InetAddress host;
	private Integer port;
	private EDRDeserialiser deserialiser;

	/**
	 * Create a {@link SimpleTcpTransportClient} with the specified host and port, and initialise it
	 */
	public SimpleTcpTransportClient(InetAddress host, int port, EDRDeserialiser deserialiser) {
		setHost(host);
		setPort(port);
		setEDRDeserialiser(deserialiser);
		initialise();
	}

	/**
	 * Create a {@link SimpleTcpTransportClient} with the specified host and port, and initialise it
	 */
	public SimpleTcpTransportClient(String host, int port, EDRDeserialiser deserialiser) throws UnknownHostException {
		this(InetAddress.getByName(host), port, deserialiser);
	}

	/**
	 * Create an uninitialised {@link SimpleTcpTransportClient}
	 */
	public SimpleTcpTransportClient() {
	}

	@Override
	protected void doInitialise() {
		requireNotNull(host, "host");
		requireNotNull(port, "port");
		if (deserialiser == null) {
			deserialiser = new OgreWireFormatDeserialiser();
		}
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
	 * Load the type domain that the {@link SimpleTcpTransportServer} provides. Since {@link SimpleTcpTransportServer}
	 * only exposes a single type domain, the argument is ignored
	 */
	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		byte[] message = doEnvelopedRequest(RequestType.CODE_TYPE_DOMAIN);
		return deserialiser.deserialiseTypeDomain(message);
	}


	/**
	 * Load the type object graph that the {@link SimpleTcpTransportServer} provides. Since {@link SimpleTcpTransportServer}
	 * only exposes a single object graph, the arguments are ignored
	 */
	@Override
	public GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
		byte[] message = doEnvelopedRequest(RequestType.CODE_OBJECT_GRAPH);
		return deserialiser.deserialiseGraphUpdate(message, typeDomain);
	}

	/**
	 * Subscribe to the graph updates that the {@link SimpleTcpTransportServer} provides. Since {@link SimpleTcpTransportServer}
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
						try {
							byte[] message = Envelope.readEnvelopedBytes(inputStream);
							if (message == null) {
								if (OgreLog.isInfoEnabled()) {
									OgreLog.info("SimpleTcpTransportClient: connection from server closed");
								}
								return;
							}
							GraphUpdate update = deserialiser.deserialiseGraphUpdate(message, typeDomain);
							if (OgreLog.isDebugEnabled()) {
								OgreLog.debug("SimpleTcpTransportClient: received new update message: "
										+ EDRDescriber.describeGraphUpdate(update));
							}
							listener.acceptGraphUpdate(update);
						} catch (IOException e) {
							OgreLog.error("Could not read data: " + e.getMessage());
						}
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
