/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * An individual property on an entity type. {@link Property} is to {@link EntityType} as
 * java.lang.reflect.Method is to java.lang.Class.
 * 
 * @author Bernie Sumption
 */
public class Property {

	private static final String[] TYPECODE_NAMES = { "int32", "int64", "float", "double", "string", "byte-array",
			"reference" };

	public static final int TYPECODE_INT32 = 0;
	public static final int TYPECODE_INT64 = 1;
	public static final int TYPECODE_FLOAT = 2;
	public static final int TYPECODE_DOUBLE = 3;
	public static final int TYPECODE_STRING = 4;
	public static final int TYPECODE_BYTES = 5;
	// REFERENCE Property objects can be cast to ReferenceProperty to access more information
	public static final int TYPECODE_REFERENCE = 6;

	private final String name;
	private final int typeCode;
	private final boolean nullable;

	private final String toStringCache;

	private EntityType entityType;
	private int propertyIndex;

	public Property(String name, int typeCode, boolean nullable) {
		if (typeCode < 0 || typeCode >= TYPECODE_NAMES.length) {
			throw new OgreException(typeCode + " is not a valid typecode.");
		}
		this.typeCode = typeCode;
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

	public static String getNameForTypecode(int typeCode) {
		return TYPECODE_NAMES[typeCode];
	}

	/**
	 * @private
	 */
	void initialise(EntityType entityType, int propertyIndex) {
		this.entityType = entityType;
		this.propertyIndex = propertyIndex;
	}

}
