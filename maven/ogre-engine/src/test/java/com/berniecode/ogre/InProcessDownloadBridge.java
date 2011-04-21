package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.server.DataSource;
import com.berniecode.ogre.wireformat.OgreWireFormatV1Serialiser;

/**
 * A {@link DownloadClientAdapter} that wraps a {@link ServerEngineTest}, directly transferring any
 * requests to it (normally, a DownloadClientAdapter would send the requestBuilder over some kind of
 * network transport, e.g. a HTTP requestBuilder).
 * 
 * @author Bernie Sumption
 */
public class InProcessDownloadBridge implements DownloadClientAdapter {
	
	OgreWireFormatV1Serialiser ser = new OgreWireFormatV1Serialiser();

	private final DataSource dataSource;

	public InProcessDownloadBridge(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		if (!typeDomainId.equals(dataSource.getTypeDomain().getTypeDomainId())) {
			throw new NoSuchThingException("There is no type domain with id '" + typeDomainId + "'");
		}
		return ser.deserialiseTypeDomain(ser.serialiseTypeDomain(dataSource.getTypeDomain()));
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
		return ser.deserialiseGraphUpdate(ser.serialiseGraphUpdate(objectGraph), typeDomain);
	}

}
