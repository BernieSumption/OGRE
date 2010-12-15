package com.berniecode.ogre.enginelib.shared;

/**
 * A reference to an {@link Entity}
 *
 * @author Bernie Sumption
 */
public class ReferencePropertyType implements PropertyType {
	
	private final String entityName;

	public ReferencePropertyType(String entityName) {
		this.entityName = entityName;
	}

	public String getDescription() {
		return "reference to " + entityName;
	}

	public String getEntityName() {
		return entityName;
	}

}
