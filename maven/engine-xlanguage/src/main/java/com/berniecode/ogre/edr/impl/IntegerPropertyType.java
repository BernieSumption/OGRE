package com.berniecode.ogre.edr.impl;

import com.berniecode.ogre.edr.PropertyType;

public class IntegerPropertyType implements PropertyType {

	public static final IntegerMetadataKey BITLENGTH = new IntegerMetadataKey("bitlength", 32);
	public static final BooleanMetadataKey NULLABLE = new BooleanMetadataKey("nullable", false);

	@Override
	public String getName() {
		return "integer";
	}

}
