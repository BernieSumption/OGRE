package com.berniecode.ogre.enginelib.server;

import com.berniecode.ogre.enginelib.shared.UpdateMessage;

/**
 * Publishes {@link UpdateMessage}s onto a publish/subscribe messaging channel
 *
 * @author Bernie Sumption
 */
public interface MessageServerAdapter {
	
	void publishUpdateMessage(UpdateMessage message);
}
