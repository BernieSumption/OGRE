package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A {@link Property} that refers to another entity, containing metadata on the type of entity that can be referred to
 *
 * @author Bernie Sumption
 */
public class ReferenceProperty extends Property {

	private EntityType referenceType;
	private final String referenceTypeName;
	private final String toStringCache;

	public ReferenceProperty(int propertyIndex, String name, String referenceTypeName) {
		super(propertyIndex, name, TYPECODE_REFERENCE, true);
		this.referenceTypeName = referenceTypeName;
		toStringCache = "reference to " + referenceTypeName + " property " + getName();
	}

	public EntityType getReferenceType() {
		return referenceType;
	}
	
	public String toString() {
		return toStringCache;
	}

	/**
	 * @private
	 */
	void setEntityType(EntityType entityType) {
		referenceType = entityType.getTypeDomain().getEntityTypeByName(referenceTypeName);
		if (referenceType == null) {
			throw new OgreException("Can't initialise property '" + this + "' because the type domain does not contain a type '" + referenceTypeName + "'");
		}
	}

}
