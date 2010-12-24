package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;


/**
 * A reference to an {@link Entity}
 *
 * @author Bernie Sumption
 */
public class ReferencePropertyType implements PropertyType {
	
	private final String entityName;
	private EntityType entityType;

	public ReferencePropertyType(String entityName) {
		this.entityName = entityName;
	}

	public String getDescription() {
		return "reference to " + entityName;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	void setTypeDomain(TypeDomain typeDomain) {
		entityType = typeDomain.getEntityTypeByName(entityName);
		if (entityType == null) {
			throw new OgreException("Can't initialise property type '" + getDescription() + "' because the type omain does not contain a type '" + entityName + "'");
		}
	}

}
