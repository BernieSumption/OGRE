package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.platformhooks.IOFailureException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * A blocking request/response mechanism to fetch snapshots and type domains from an OGRE server.
 * 
 * @author Bernie Sumption
 */
public interface DownloadClientAdapter {

	/**
	 * Load a representation of a type domain
	 */
	TypeDomain loadTypeDomain(String typeDomainId) throws IOFailureException, NoSuchThingException;
}
