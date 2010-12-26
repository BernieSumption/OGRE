package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.client.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.server.ServerEngine;
import com.berniecode.ogre.enginelib.server.ServerEngineTest;
import com.berniecode.ogre.enginelib.shared.GraphUpdate;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
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
		//TODO factor the byte[] stage out into RawDownloadClientAdapter
		return ser.deserialiseTypeDomain(ser.serialiseTypeDomain(server.getTypeDomain(typeDomainId)));
	}

	@Override
	public GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
		GraphUpdate objectGraph = server.getObjectGraph(typeDomain.getTypeDomainId(), objectGraphId);
		//TODO factor the byte[] stage out into RawDownloadClientAdapter
		return ser.deserialiseGraphUpdate(ser.serialiseGraphUpdate(objectGraph), typeDomain);
	}

}
