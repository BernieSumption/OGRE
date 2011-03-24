package com.berniecode.ogre.tcpbridge;

import com.berniecode.ogre.enginelib.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;

/**
 * A download client compatible with {@link TcpBridgeServer}
 *
 * @author Bernie Sumption
 */
public class TcpDownloadClient implements DownloadClientAdapter {

	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) throws NoSuchThingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphUpdate loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
		// TODO Auto-generated method stub
		return null;
	}

}
