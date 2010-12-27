package com.berniecode.ogre.server;

import com.berniecode.ogre.enginelib.GraphUpdate;

/**
 * Publishes {@link GraphUpdate}s onto a publish/subscribe messaging channel
 *
 * @author Bernie Sumption
 */
public interface MessageServerAdapter {
	
	void publishGraphUpdate(GraphUpdate update);
}
