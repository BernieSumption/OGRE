package com.berniecode.ogre.enginelib.server;

import com.berniecode.ogre.enginelib.shared.ObjectGraphUpdate;

/**
 * Publishes {@link ObjectGraphUpdate}s onto a publish/subscribe messaging channel
 *
 * @author Bernie Sumption
 */
public interface MessageServerAdapter {
	
	void publishUpdateMessage(ObjectGraphUpdate message);
}
