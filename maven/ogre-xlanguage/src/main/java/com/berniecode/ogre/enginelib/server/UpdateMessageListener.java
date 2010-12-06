package com.berniecode.ogre.enginelib.server;

import com.berniecode.ogre.enginelib.shared.UpdateMessage;

/**
 * An object that can accept update messages
 *
 * @author Bernie Sumption
 */
public interface UpdateMessageListener {

	void acceptUpdateMessage(UpdateMessage message);
}
