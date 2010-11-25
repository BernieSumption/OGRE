package com.berniecode.ogre.edr.impl;

import com.berniecode.ogre.edr.MetadataKey;

/**
 * A bit of metadata holding an integer.
 * 
 * @author Bernie Sumption
 */
class IntegerMetadataKey implements MetadataKey<Integer> {

	private final Integer defaultValue;
	private final String name;

	public IntegerMetadataKey(String name, Integer defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public Integer fromString(String value) {
		return new Integer(value);
	}

	@Override
	public Integer getDefault() {
		return defaultValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString(Integer value) {
		return value.toString();
	}

}
