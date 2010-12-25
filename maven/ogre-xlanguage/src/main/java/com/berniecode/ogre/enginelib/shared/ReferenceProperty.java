package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A {@link Property} that refers to another entity, containing metadata on the type of entity that can be referred to
 *
 * @author Bernie Sumption
 */
public class ReferenceProperty extends Property {

	private EntityType referenceType;
	private final String referenceTypeName;

	public ReferenceProperty(int propertyIndex, String name, String referenceTypeName) {
		super(propertyIndex, name, TYPECODE_REFERENCE, true);
		this.referenceTypeName = referenceTypeName;
	}

	public EntityType getReferenceType() {
		return referenceType;
	}
	
	public String toString() {
		return "reference to " + referenceTypeName + " property " + getName();
	}


	void setTypeDomain(TypeDomain typeDomain) {
		referenceType = typeDomain.getEntityTypeByName(referenceTypeName);
		if (referenceType == null) {
			throw new OgreException("Can't initialise property '" + this + "' because the type domain does not contain a type '" + referenceTypeName + "'");
		}
	}

}
