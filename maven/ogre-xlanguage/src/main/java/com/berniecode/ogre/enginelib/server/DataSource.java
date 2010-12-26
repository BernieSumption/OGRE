package com.berniecode.ogre.enginelib.server;

import com.berniecode.ogre.enginelib.shared.GraphUpdate;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.enginelib.shared.GraphUpdateListener;

/**
 * A ServerDataAdapter is a view onto a single object graph that converts the object graph into
 * OGRE's Entity Data Representation. The object graph can be any structured data. By writing new
 * ServerDataAdapters, you can cause OGRE to run off any data source.
 * 
 * @author Bernie Sumption
 */
public interface DataSource {

	TypeDomain getTypeDomain();

	/**
	 * @return the ID of the object graph produced by this data source
	 */
	String getObjectGraphId();

	/**
	 * @return A snapshot of the state of the object graph
	 */
	GraphUpdate createSnapshot();

	/**
	 * Set the listener that will be notified about updates to the object graph.
	 */
	void setGraphUpdateListener(GraphUpdateListener listener);
}
