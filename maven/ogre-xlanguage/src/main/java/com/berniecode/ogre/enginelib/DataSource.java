package com.berniecode.ogre.enginelib;


/**
 * A ServerDataAdapter is a view onto a single object graph that converts the object graph into EDR
 * (Entity Data Representation). The object graph can be any structured data. By writing new
 * DataSource implementations, you can cause OGRE to run off any source of structured data.
 * 
 * <p>
 * Implementations do not need to be thread safe, or perform any caching
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
