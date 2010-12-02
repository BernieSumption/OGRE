package com.berniecode.ogre;


public class EntityClassWithAllFields {

	private Integer nullableInt;
	private int nonNullableInt;

	public int getNonNullableInt() {
		return nonNullableInt;
	}

	public void setNonNullableInt(int nonNullableInt) {
		this.nonNullableInt = nonNullableInt;
	}

	public Integer getNullableInt() {
		return nullableInt;
	}

	public void setNullableInt(Integer nullableInt) {
		this.nullableInt = nullableInt;
	}
	
	public static final String EXPECTED_ENTITY_TYPE = "";

}