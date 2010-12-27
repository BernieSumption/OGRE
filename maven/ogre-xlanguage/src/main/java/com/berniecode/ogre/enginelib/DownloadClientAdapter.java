package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;

/**
 * A blocking request/response mechanism to fetch snapshots and type domains from an OGRE server.
 * 
 * @author Bernie Sumption
 */
public interface DownloadClientAdapter {

	/**
	 * Load a representation of a type domain
	 */
	TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException;

	/**
	 * Load a snapshot of the state of an object graph 
	 */
	GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException;
}
