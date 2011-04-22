package com.berniecode.ogre;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.MessageClientAdapter;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.wireformat.OgreWireFormatDeserialiser;
import com.berniecode.ogre.wireformat.OgreWireFormatSerialiser;

public class InProcessMessageBridge implements GraphUpdateListener, MessageClientAdapter {

	OgreWireFormatSerialiser ser = new OgreWireFormatSerialiser();
	OgreWireFormatDeserialiser dser = new OgreWireFormatDeserialiser();
	
	private int messageCount = 0;
	private GraphUpdate lastGraphUpdate;
	
	List<ListenerHolder> holders = new ArrayList<ListenerHolder>();

	@Override
	public void acceptGraphUpdate(GraphUpdate update) {
		lastGraphUpdate = update;
		messageCount++;
		String key = getKey(update.getTypeDomain(), update.getObjectGraphId());
		for (ListenerHolder holder: holders) {
			if (holder.key.equals(key)) {
				holder.listener.acceptGraphUpdate(dser.deserialiseGraphUpdate(ser.serialiseGraphUpdate(update), holder.typeDomain));
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