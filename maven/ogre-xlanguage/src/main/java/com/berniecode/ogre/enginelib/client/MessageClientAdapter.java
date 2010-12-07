package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.shared.UpdateMessageListener;

/**
 * Receives messages from a publish/subscribe messaging channel
 *
 * @author Bernie Sumption
 */
public interface MessageClientAdapter {
	
	/**
	 * Subscribe to update messages relating to a specific object graph
	 * 
	 * @param typeDomainId
	 * @param objectGraphId
	 * @param listener
	 */
	void subscribeToUpdateMessages(String typeDomainId, String objectGraphId, UpdateMessageListener listener);

}
