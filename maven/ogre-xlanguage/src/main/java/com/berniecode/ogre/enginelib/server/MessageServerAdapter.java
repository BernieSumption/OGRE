package com.berniecode.ogre.enginelib.server;

import com.berniecode.ogre.enginelib.shared.GraphUpdate;

/**
 * Publishes {@link GraphUpdate}s onto a publish/subscribe messaging channel
 *
 * @author Bernie Sumption
 */
public interface MessageServerAdapter {
	
	void publishUpdateMessage(GraphUpdate message);
}
