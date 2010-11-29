package com.berniecode.ogre.engine.client;

import com.berniecode.ogre.engine.IOFailureException;
import com.berniecode.ogre.engine.NoSuchThingException;
import com.berniecode.ogre.engine.shared.TypeDomain;

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
