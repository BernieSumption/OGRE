package com.berniecode.ogre;

public interface EntityClassWithAllFields {

	Integer getNullableInt();

	int getNonNullableInt();

	long getNonNullableLong();

	Long getNullableLong();

	String getString();

	float getNonNullableFloat();

	Float getNullableFloat();

	double getNonNullableDouble();

	Double getNullableDouble();

	byte[] getBytes();

	EntityElement getEntityElement();

}