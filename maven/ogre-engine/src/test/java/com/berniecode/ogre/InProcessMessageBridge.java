package com.berniecode.ogre;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.enginelib.client.MessageClientAdapter;
import com.berniecode.ogre.enginelib.server.MessageServerAdapter;
import com.berniecode.ogre.enginelib.shared.GraphUpdate;
import com.berniecode.ogre.enginelib.shared.GraphUpdateListener;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.wireformat.OgreWireFormatV1Serialiser;

public class InProcessMessageBridge implements MessageServerAdapter, MessageClientAdapter {
	
	OgreWireFormatV1Serialiser ser = new OgreWireFormatV1Serialiser();
	
	private int messageCount = 0;
	private GraphUpdate lastGraphUpdate;
	
	List<ListenerHolder> holders = new ArrayList<ListenerHolder>();

	@Override
	public void publishGraphUpdate(GraphUpdate update) {
		lastGraphUpdate = update;
		messageCount++;
		String key = getKey(update.getTypeDomain(), update.getObjectGraphId());
		for (ListenerHolder holder: holders) {
			if (holder.key.equals(key)) {
				//TODO factor the byte[] stage out into RawMessage*Adapter
				holder.listener.acceptGraphUpdate(ser.deserialiseGraphUpdate(ser.serialiseGraphUpdate(update), holder.typeDomain));
			}
		}
	}

	@Override
	public void subscribeToGraphUpdates(TypeDomain typeDomain, String objectGraphId, GraphUpdateListener listener) {
		holders.add(new ListenerHolder(getKey(typeDomain, objectGraphId), listener, typeDomain));
	}

	private String getKey(TypeDomain typeDomain, String objectGraphId) {
		return typeDomain.getTypeDomainId() + "/" + objectGraphId;
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
	public final TypeDomain typeDomain;
	public ListenerHolder(String key, GraphUpdateListener listener, TypeDomain typeDomain) {
		this.key = key;
		this.listener = listener;
		this.typeDomain = typeDomain;
	}
}