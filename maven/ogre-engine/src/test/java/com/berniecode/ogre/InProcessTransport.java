/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.enginelib.ClientTransportAdapter;
import com.berniecode.ogre.enginelib.DataSource;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.wireformat.OgreWireFormatDeserialiser;
import com.berniecode.ogre.wireformat.OgreWireFormatSerialiser;

/**
 * A {@link ClientTransportAdapter} that wraps a {@link DataSource}, directly transferring any
 * requests to it (normally, a ClientTransportAdapter would send the requestBuilder over some kind of
 * network transport, e.g. a HTTP requestBuilder).
 * 
 * @author Bernie Sumption
 */
public class InProcessTransport implements ClientTransportAdapter, GraphUpdateListener {

	OgreWireFormatSerialiser ser = new OgreWireFormatSerialiser();
	OgreWireFormatDeserialiser dser = new OgreWireFormatDeserialiser();

	private final DataSource dataSource;

	public InProcessTransport(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		if (!typeDomainId.equals(dataSource.getTypeDomain().getTypeDomainId())) {
			throw new NoSuchThingException("There is no type domain with id '" + typeDomainId + "'");
		}
		return dser.deserialiseTypeDomain(ser.serialiseTypeDomain(dataSource.getTypeDomain()));
	}

	@Override
	public GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
		if (!typeDomain.getTypeDomainId().equals(dataSource.getTypeDomain().getTypeDomainId())) {
			throw new NoSuchThingException("There is no type domain with id '" + typeDomain.getTypeDomainId() + "'");
		}
		if (!objectGraphId.equals(dataSource.getObjectGraphId())) {
			throw new NoSuchThingException("There is no object graph with id '" + objectGraphId + "'");
		}
		GraphUpdate objectGraph = dataSource.createSnapshot();
		return dser.deserialiseGraphUpdate(ser.serialiseGraphUpdate(objectGraph), typeDomain);
	}
	
	//
	// GRAPH UPDATES
	//
	
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
