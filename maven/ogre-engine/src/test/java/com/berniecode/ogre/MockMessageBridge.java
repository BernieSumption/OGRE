package com.berniecode.ogre;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.enginelib.client.MessageClientAdapter;
import com.berniecode.ogre.enginelib.server.MessageServerAdapter;
import com.berniecode.ogre.enginelib.shared.ObjectGraphUpdate;
import com.berniecode.ogre.enginelib.shared.UpdateMessageListener;

public class MockMessageBridge implements MessageServerAdapter, MessageClientAdapter {
	
	private int messageCount = 0;
	private ObjectGraphUpdate lastUpdateMessage;
	
	List<ListenerHolder> holders = new ArrayList<ListenerHolder>();

	@Override
	public void publishUpdateMessage(ObjectGraphUpdate message) {
		lastUpdateMessage = message;
		messageCount++;
		String key = getKey(message.getTypeDomainId(), message.getObjectGraphId());
		for (ListenerHolder holder: holders) {
			if (holder.key.equals(key)) {
				holder.listener.acceptUpdateMessage(message);
			}
		}
	}

	@Override
	public void subscribeToUpdateMessages(String typeDomainId, String objectGraphId, UpdateMessageListener listener) {
		holders.add(new ListenerHolder(getKey(typeDomainId, objectGraphId), listener));
	}

	private String getKey(String typeDomainId, String objectGraphId) {
		return typeDomainId + "/" + objectGraphId;
	}

	public void resetMessageCount() {
		messageCount = 0;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public ObjectGraphUpdate getLastUpdateMessage() {
		return lastUpdateMessage;
	}

}

class ListenerHolder {
	public final String key;
	public final UpdateMessageListener listener;
	public ListenerHolder(String key, UpdateMessageListener listener) {
		this.key = key;
		this.listener = listener;
	}
}