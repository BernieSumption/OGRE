package com.berniecode.ogre.enginelib.platformhooks;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.Property;

/**
 * Cross-language operations for working with values
 *
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
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
	 * Arrays should be compared by contents, so two arrays are equivalent if they contain
	 * equivalent items in the same order
	 * 
	 * <p>
	 * Two null values are considered equivalent, one null value and one non-null value not
	 * equivalent
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
		if (isArray(value1) && isArray(value2)) {
			return arraysAreEquivalent(value1, value2);
		}
		return value1.equals(value2);
	}

	private static boolean arraysAreEquivalent(Object value1, Object value2) {
		int length = getArrayLength(value1);
		if (length != getArrayLength(value2)) {
			return false;
		}
		for (int i=0; i<length; i++) {
			if (!valuesAreEquivalent(getArrayValue(value1, i), getArrayValue(value2, i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Perform a shallow copy of an array.
	 * 
	 * @return a new array of the same type as the specified array, containing the same values.
	 */
	public static Object[] cloneArray(Object[] values) {
		return Arrays.copyOf(values, values.length);
	}

	/**
	 * Check if a value is an array
	 */
	public static boolean isArray(Object value) {
		return value != null && value.getClass().isArray();
	}

	/**
	 * Return an item from a specific position in an array.
	 * 
	 * <p>
	 * This is required in situations where code needs to be able to access any kind of array, since
	 * as far as I can tell there's no way to write code in Java that is capable of iterating over
	 * both an array of objects and an array of primitives without knowing the array's component
	 * type in advance
	 */
	public static Object getArrayValue(Object array, int index) {
		return Array.get(array, index);
	}

	/**
	 * Return the length of an array
	 */
	public static int getArrayLength(Object array) {
		return Array.getLength(array);
	}

	/**
	 * Coerce a {@code Long} object into a native long value
	 */
	public static long objectToId(Object id) {
		return ((Long) id).longValue();
	}

	/**
	 * Coerce a native long value into a {@code Long} object
	 */
	public static Long idToObject(long id) {
		return Long.valueOf(id);
	}

	/**
	 * Check whether an arbitrary value is a suitable runtime type for a specific property
	 * 
	 * @throws OgreException if the value is not of the correct type
	 */
	public static void validatePropertyValue(Property property, Object object, boolean wired) {
		if (object == null) {
			if (!property.isNullable()) {
				throw new OgreException("Invalid value for " + property + ": null values are not permitted");
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
			requiredClass = wired ? Entity.class : Long.class;
			break;
		default:
			throw new OgreException(property + " has invalid invalid typeCode: " + property.getTypeCode());
		}
		if (!requiredClass.isAssignableFrom(object.getClass())) {
			throw new OgreException("Invalid value for " + property + ": expected " + requiredClass + ", found " + object.getClass());
		}
	}

}
