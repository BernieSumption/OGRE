package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.client.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

public class MockDownloadClientAdapter implements DownloadClientAdapter {

	private final TypeDomain typeDomain;

	public MockDownloadClientAdapter(TypeDomain typeDomain) {
		this.typeDomain = typeDomain;
	}

	public TypeDomain loadTypeDomain(String typeDomainId) {
		return typeDomain;
	}

}
