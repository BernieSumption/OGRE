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

package com.berniecode.ogre.enginelib.platformhooks;

import java.util.Arrays;

import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.ReferenceProperty;

/**
 * Cross-language operations for working with values
 *
 * @author Bernie Sumption
 */
public class ValueUtils {
	
	private ValueUtils() {}

	/**
	 * Check whether two values are equivalent according to the following rules.
	 * 
	 * <p>
	 * Generally speaking this should follow the same contract as
	 * {@link java.lang.Object#equals(Object)}, i.e. it should compare the values that objects
	 * represent, not the objects themselves.
	 * 
	 * <p>
	 * It can be assumed that the values being compared are of the same type, so implementations
	 * don't need to consider what to happen when for example the 32 bit integer '42' is compared
	 * with the 64 bit integer '42'
	 */
	public static boolean valuesAreEquivalent(Object value1, Object value2) {
		if (value1 == null && value2 == null) {
			return true;
		}
		if (value1 == null || value2 == null) {
			return false;
		}
		if (value1 instanceof byte[]) {
			return Arrays.equals((byte[]) value1, (byte[]) value2);
		}
		return value1.equals(value2);
	}

	/**
	 * Unbox an object created with {@link #idToObject(long)}
	 */
	public static long objectToId(Object id) {
		return ((Long) id).longValue();
	}

	/**
	 * Box a native long value into an object reference
	 */
	public static Long idToObject(long id) {
		return Long.valueOf(id);
	}
	
	/**
	 * Check whether an arbitrary value is a suitable runtime type for a specific property.
	 * 
	 * @throws OgreException if the value is not of the correct type
	 */
	public static void validatePropertyValue(Property property, Object object) {
		if (object == null) {
			if (!property.isNullable()) {
				throw new InvalidGraphUpdateException("Invalid value for " + property + ": null values are not permitted");
			}
			return;
		}
		Class requiredClass;
		switch(property.getTypeCode()) {
		case Property.TYPECODE_INT32:
			requiredClass = Integer.class;
			break;
		case Property.TYPECODE_INT64:
			requiredClass = Long.class;
			break;
		case Property.TYPECODE_FLOAT:
			requiredClass = Float.class;
			break;
		case Property.TYPECODE_DOUBLE:
			requiredClass = Double.class;
			break;
		case Property.TYPECODE_STRING:
			requiredClass = String.class;
			break;
		case Property.TYPECODE_BYTES:
			requiredClass = byte[].class;
			break;
		case Property.TYPECODE_REFERENCE:
			requiredClass = Entity.class;
			break;
		default:
			throw new InvalidGraphUpdateException(property + " has invalid invalid typeCode: " + property.getTypeCode());
		}
		if (!requiredClass.isInstance(object)) {
			throw new InvalidGraphUpdateException("Invalid value for " + property + ": expected " + requiredClass + ", found " + object.getClass());
		}
		if (property.getTypeCode() == Property.TYPECODE_REFERENCE) {
			Entity entity = (Entity) object;
			if (entity.getEntityType() != ((ReferenceProperty) property).getReferenceType()) {
				throw new InvalidGraphUpdateException("Invalid value for " + property + ": expected entity of type " + entity.getEntityType() + ", found entity of type " + ((ReferenceProperty) property).getReferenceType());
			}
		}
	}

	/**
	 * @return A string representation of a value. This should be able to handle any of the values
	 *         acceptable to {@link #validatePropertyValue(Property, Object)}
	 */
	public static Object valueToString(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof byte[]) {
			byte[] bs = (byte[]) value;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bs.length; i++) {
				if (i != 0) {
					sb.append(',');
				}
				sb.append(bs[i]);
			}
			return sb.toString();
		}
		return value.toString();
	}

}
