package com.berniecode.ogre.edr.impl;

import com.berniecode.ogre.edr.MetadataKey;

/**
 * A bit of metadata holding a boolean value.
 * 
 * @author Bernie Sumption
 */
class BooleanMetadataKey implements MetadataKey<Boolean> {

	private final Boolean defaultValue;
	private final String name;

	public BooleanMetadataKey(String name, Boolean defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public Boolean fromString(String value) {
		return "true".equals(value);
	}

	@Override
	public Boolean getDefault() {
		return defaultValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString(Boolean value) {
		if (value) {
			return "true";
		}
		return "false";
	}

}
