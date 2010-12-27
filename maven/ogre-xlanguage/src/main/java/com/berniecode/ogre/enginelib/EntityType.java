package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.ArrayBuilder;

/**
 * A description of an entity. EntityType is to {@link Entity} as java.lang.Class is to
 * java.lang.Object.
 * 
 * @author Bernie Sumption
 */
public class EntityType {

	private final int index;
	private final String name;
	private final Property[] properties;
	private Property[] referenceProperties;
	private TypeDomain typeDomain;

	public EntityType(int index, String name, Property[] properties) {
		this.index = index;
		this.name = name;
		this.properties = properties;
		
		ArrayBuilder builder = new ArrayBuilder(Property.class);
		for (int i=0; i<properties.length; i++) {
			if (properties[i] instanceof ReferenceProperty) {
				builder.add(properties[i]);
			}
		}
		referenceProperties = (Property[]) builder.buildArray();
	}

	/**
	 * @return The index of this {@link EntityType} in its parent {@link TypeDomain}
	 */
	public int getEntityTypeIndex() {
		return index;
	}

	/**
	 * @return The name of this entity type, typically a fully qualified class name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The {@link Property}s of this entity type.
	 */
	public Property getProperty(int propertyIndex) {
		return properties[propertyIndex];
	}

	/**
	 * @return The number of {@link Property}s in this entity type.
	 */
	public int getPropertyCount() {
		return properties.length;
	}
	
	public String toString() {
		return name;
	}

	/**
	 * @return an array of all the reference properties in this {@link EntityType}
	 */
	public Property[] getReferenceProperties() {
		return referenceProperties;
	}
	
	/**
	 * The {@link TypeDomain} that this {@link EntityType} belongs to.
	 */
	public TypeDomain getTypeDomain() {
		return typeDomain;
	}

	void setTypeDomain(TypeDomain typeDomain) {
		this.typeDomain = typeDomain;
		for (int i = 0; i < referenceProperties.length; i++) {
			((ReferenceProperty) referenceProperties[i]).setTypeDomain(typeDomain);
		}
	}
}
