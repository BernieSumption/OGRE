package com.berniecode.ogre.engine.server;

import com.berniecode.ogre.engine.shared.TypeDomain;

/**
 * A ServerDataAdapter is a view onto a single object graph that converts the object graph into
 * OGRE's Entity Data Representation. The object graph can be any structured data. By writing new
 * ServerDataAdapters, you can cause OGRE to run off any data source.
 * 
 * @author Bernie Sumption
 */
public interface DataSource {

	public TypeDomain getTypeDomain();

	// TODO String getObjectGraphId()
	// TODO long[] getEntityIdsOfType(EntityType type)
	// TODO Entity getEntity(EntityType type, long id)
	// TODO addChangeEventListener(ChangeEventListener listener) reports changes to the object graph
}
