package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.server.ServerEngine;
import com.berniecode.ogre.server.ServerEngineTest;
import com.berniecode.ogre.wireformat.OgreWireFormatV1Serialiser;

/**
 * A {@link DownloadClientAdapter} that wraps a {@link ServerEngineTest}, directly transferring any
 * requests to it (normally, a DownloadClientAdapter would send the request over some kind of
 * network transport, e.g. a HTTP request).
 * 
 * @author Bernie Sumption
 */
public class InProcessDownloadBridge implements DownloadClientAdapter {
	
	OgreWireFormatV1Serialiser ser = new OgreWireFormatV1Serialiser();

	private final ServerEngine server;

	public InProcessDownloadBridge(ServerEngine server) {
		this.server = server;
	}

	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		return ser.deserialiseTypeDomain(ser.serialiseTypeDomain(server.getTypeDomain(typeDomainId)));
	}

	@Override
	public GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
		GraphUpdate objectGraph = server.getObjectGraph(typeDomain.getTypeDomainId(), objectGraphId);
		return ser.deserialiseGraphUpdate(ser.serialiseGraphUpdate(objectGraph), typeDomain);
	}

}
