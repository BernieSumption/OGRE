package com.berniecode.ogre;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.enginelib.client.MessageClientAdapter;
import com.berniecode.ogre.enginelib.server.MessageServerAdapter;
import com.berniecode.ogre.enginelib.shared.GraphUpdate;
import com.berniecode.ogre.enginelib.shared.GraphUpdateListener;

public class InProcessMessageBridge implements MessageServerAdapter, MessageClientAdapter {
	
	private int messageCount = 0;
	private GraphUpdate lastGraphUpdate;
	
	List<ListenerHolder> holders = new ArrayList<ListenerHolder>();

	@Override
	public void publishGraphUpdate(GraphUpdate update) {
		lastGraphUpdate = update;
		messageCount++;
		String key = getKey(update.getTypeDomainId(), update.getObjectGraphId());
		for (ListenerHolder holder: holders) {
			if (holder.key.equals(key)) {
				holder.listener.acceptGraphUpdate(update);
			}
		}
	}

	@Override
	public void subscribeToGraphUpdates(String typeDomainId, String objectGraphId, GraphUpdateListener listener) {
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

	public GraphUpdate getLastGraphUpdate() {
		return lastGraphUpdate;
	}

}

class ListenerHolder {
	public final String key;
	public final GraphUpdateListener listener;
	public ListenerHolder(String key, GraphUpdateListener listener) {
		this.key = key;
		this.listener = listener;
	}
}