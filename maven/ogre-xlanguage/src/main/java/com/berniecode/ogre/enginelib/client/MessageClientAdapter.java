package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.shared.GraphUpdateListener;

/**
 * Receives messages from a publish/subscribe messaging channel
 *
 * @author Bernie Sumption
 */
public interface MessageClientAdapter {
	
	/**
	 * Subscribe to graph updates relating to a specific object graph
	 */
	void subscribeToGraphUpdates(String typeDomainId, String objectGraphId, GraphUpdateListener listener);

}
