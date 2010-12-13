package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.client.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.server.ServerEngine;
import com.berniecode.ogre.enginelib.server.ServerEngineTest;
import com.berniecode.ogre.enginelib.shared.ObjectGraphValueMessage;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * A {@link DownloadClientAdapter} that wraps a {@link ServerEngineTest}, directly transferring any
 * requests to it (normally, a DownloadClientAdapter would send the request over some kind of
 * network transport, e.g. a HTTP request).
 * 
 * @author Bernie Sumption
 */
public class InProcessDownloadBridge implements DownloadClientAdapter {

	private final ServerEngine server;

	public InProcessDownloadBridge(ServerEngine server) {
		this.server = server;
	}

	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		return server.getTypeDomain(typeDomainId);
	}

	@Override
	public ObjectGraphValueMessage loadObjectGraph(String typeDomainId, String objectGraphId) throws NoSuchThingException {
		return server.getObjectGraph(typeDomainId, objectGraphId);
	}

}