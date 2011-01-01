package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * An individual property on an entity type. {@link Property} is to {@link EntityType} as
 * java.lang.reflect.Method is to java.lang.Class.
 * 
 * @author Bernie Sumption
 */
public class Property {
	
	private static final String[] TYPECODE_NAMES = {"int", "float", "double", "string", "byte-array", "reference"};
	
	// INT Property objects can be cast to IntegerProperty to access more information
    public static final int TYPECODE_INT       = 0;
    public static final int TYPECODE_FLOAT     = 1;
    public static final int TYPECODE_DOUBLE    = 2;
    public static final int TYPECODE_STRING    = 3;
    public static final int TYPECODE_BYTES     = 4;
	// REFERENCE Property objects can be cast to ReferenceProperty to access more information
    public static final int TYPECODE_REFERENCE = 5;
    

	private final int propertyIndex;
	private final String name;
	private final int typeCode;
	private final boolean nullable;

	private final String toStringCache;

	private EntityType entityType;

	public Property(int propertyIndex, String name, int typeCode, boolean nullable) {
		if (typeCode < 0 || typeCode >= TYPECODE_NAMES.length) {
			throw new OgreException(typeCode + " is not a valid typecode.");
		}
		this.typeCode = typeCode;
		this.propertyIndex = propertyIndex;
		this.name = name;
		this.nullable = nullable;
		toStringCache = (nullable ? "nullable " : "") + TYPECODE_NAMES[typeCode] + " property " + name;
	}

	/**
	 * @return The name of this property. Property names should be lower case words separated by
	 *         underscores, e.g. "property_name". This will be transformed into the recommended
	 *         property naming convention in the client language, e.g. getPropertyName() for Java or
	 *         propertyName in ActionScript.
	 */
	public String getName() {
		return name;
	}

	/**
	 * The position of this property in the parent {@link EntityType}'s properties list.
	 */
	public int getPropertyIndex() {
		return propertyIndex;
	}
	
	public String toString() {
		return toStringCache;
	}

	/**
	 * @return The typeCode code of this property - one of the numbers defined in the TYPECODE_*
	 *         public constants of this class
	 */
	public int getTypeCode() {
		return typeCode;
	}

	public boolean isNullable() {
		return nullable;
	}
	
	/**
	 * @return The {@link EntityType} that this {@link Property} belongs to
	 */
	public EntityType getEntityType() {
		return entityType;
	}
	
	/**
	 * @private
	 */
	void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
	
	public static String getNameForTypecode(int typeCode) {
		return TYPECODE_NAMES[typeCode];
	}
	
}
