package com.berniecode.ogre.enginelib;


/**
 * Receives messages from a publish/subscribe messaging channel
 *
 * @author Bernie Sumption
 */
public interface MessageClientAdapter {
	
	/**
	 * Subscribe to graph updates relating to a specific object graph
	 */
	void subscribeToGraphUpdates(TypeDomain typeDomain, String objectGraphId, GraphUpdateListener listener);

}
