package com.berniecode.ogre;

import com.berniecode.ogre.engine.platformhooks.NativeOrderedCollection;
import com.berniecode.ogre.engine.shared.EntityType;
import com.berniecode.ogre.engine.shared.Property;
import com.berniecode.ogre.engine.shared.impl.ImmutableEntityType;

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
	
	public static final EntityType EXPECTED_ENTITY_TYPE;
	
	static {
		EXPECTED_ENTITY_TYPE = new ImmutableEntityType(
				EntityClassWithAllFields.class.getName(),
				new NativeOrderedCollection<Property>());
	}

}