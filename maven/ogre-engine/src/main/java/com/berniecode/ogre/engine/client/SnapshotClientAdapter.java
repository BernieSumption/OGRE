package com.berniecode.ogre.engine.client;

import java.io.IOException;

/**
 * An adapter for a {@link SnapshotClient} that knows how to issue requests to and fetch responses
 * from a snapshot transport mechanism.
 * 
 * @author Bernie Sumption
 */
public interface SnapshotClientAdapter {

	/**
	 * Load a representation of a type domain
	 * 
	 * @return a response encoded in the OGRE wire format
	 */
	byte[] loadTypeDomain(String typeDomainId) throws IOException;

	/**
	 * Load a snapshot of a specific object graph identified by type domain and graph ID
	 * 
	 * @return a response encoded in the OGRE wire format
	 */
	byte[] loadSnapshot(String typeDomainId, String objectGraphId) throws IOException;
}
