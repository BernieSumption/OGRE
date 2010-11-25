package com.berniecode.ogre.client;

import java.io.IOException;

import com.berniecode.ogre.edr.ObjectGraph;
import com.berniecode.ogre.edr.TypeDomain;

/**
 * A blocking request/response mechanism to fetch snapshots and type domains from an OGRE server.
 *
 * @author Bernie Sumption
 */
public interface SnapshotClient {

	/**
	 * Load a representation of a type domain
	 */
	TypeDomain loadTypeDomain(String typeDomainId) throws IOException;
	
	/**
	 * Load a snapshot of a specific object graph identified by type domain and graph ID
	 */
	ObjectGraph loadSnapshot(String typeDomainId, String objectGraphId) throws IOException;
}
