package com.berniecode.ogre.engine.client;

import com.berniecode.ogre.engine.platformhooks.NativeOrderedCollection;
import com.berniecode.ogre.engine.shared.OrderedCollection;
import com.berniecode.ogre.engine.shared.TypeDomain;

public class MockTypeDomain implements TypeDomain {

	public OrderedCollection getEntityTypes() {
		return new NativeOrderedCollection();
	}

	public String getTypeDomainId() {
		return "MockTypeDomain";
	}

}
