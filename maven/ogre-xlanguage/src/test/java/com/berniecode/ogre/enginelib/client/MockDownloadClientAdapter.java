package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.shared.ObjectGraph;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

public class MockDownloadClientAdapter implements DownloadClientAdapter {

	private final TypeDomain typeDomain;
	private final ObjectGraph objectGraph;

	public MockDownloadClientAdapter(TypeDomain typeDomain, ObjectGraph objectGraph) {
		this.typeDomain = typeDomain;
		this.objectGraph = objectGraph;
	}

	public TypeDomain loadTypeDomain(String typeDomainId) {
		return typeDomain;
	}

	public ObjectGraph loadObjectGraph(TypeDomain typeDomain, String objectGraphId) throws NoSuchThingException {
		return objectGraph;
	}

}
