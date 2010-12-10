package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.platformhooks.IOFailureException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.shared.ObjectGraph;
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

	/**
	 * Load a snapshot of the state of an object graph 
	 */
	//TODO change to ObjectGraphValue, being (typeDomainId, objectGraphId and array of EntityValue)
	//TODO change TypeDomain typeDomain to String typeDomainId, in light of above
	ObjectGraph loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException;
}
