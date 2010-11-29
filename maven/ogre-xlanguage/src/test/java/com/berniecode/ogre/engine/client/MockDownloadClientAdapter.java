package com.berniecode.ogre.engine.client;

import com.berniecode.ogre.engine.shared.TypeDomain;

public class MockDownloadClientAdapter implements DownloadClientAdapter {

	private final TypeDomain typeDomain;

	public MockDownloadClientAdapter(TypeDomain typeDomain) {
		this.typeDomain = typeDomain;
	}

	@Override
	public TypeDomain loadTypeDomain(String typeDomainId) {
		return typeDomain;
	}

}
