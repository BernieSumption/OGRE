package com.berniecode.ogre.tcpbridge;

import java.io.IOException;
import java.net.Socket;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.wireformat.OgreWireFormatV1Serialiser;

//TODO detect NoSuchThingException error messages from server and throw exceptions
public class TcpBridgeClient extends InitialisingBean implements DownloadClientAdapter {

	private String host;
	private Integer port;
	private OgreWireFormatV1Serialiser serialiser = new OgreWireFormatV1Serialiser();

	public TcpBridgeClient(String host, int port) {
		this.host = host;
		this.port = port;
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

	public void setHost(String host) {
		requireInitialised(false, "setHost()");
		this.host = host;
	}

	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		String request = "loadTypeDomain\t" + typeDomainId + "\n";
		try {
			Socket socket = null;
			try {
				socket = new Socket(host, port);
				socket.getOutputStream().write(request.getBytes("UTF8"));
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
		String request = "loadObjectGraph\t" + typeDomain.getTypeDomainId() + "\t" + objectGraphId + "\n";
		try {
			Socket socket = null;
			try {
				socket = new Socket(host, port);
				socket.getOutputStream().write(request.getBytes("UTF8"));
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

}
