package com.berniecode.ogre.engine.shared.impl;

import com.berniecode.ogre.engine.shared.PropertyType;

public class IntegerPropertyType implements PropertyType {

	public static final String BITLENGTH = "bitlength";
	public static final String NULLABLE = "nullable";

	public String getName() {
		return "integer";
	}

}
