package com.berniecode.ogre.enginelib.shared;


/**
 * An object that can accept update messages
 *
 * @author Bernie Sumption
 */
public interface UpdateMessageListener {

	void acceptUpdateMessage(UpdateMessage message);
}
