package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.platformhooks.NativeOrderedCollection;
import com.berniecode.ogre.enginelib.shared.OrderedCollection;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

public class MockTypeDomain implements TypeDomain {

	public OrderedCollection getEntityTypes() {
		return new NativeOrderedCollection();
	}

	public String getTypeDomainId() {
		return "MockTypeDomain";
	}

}
