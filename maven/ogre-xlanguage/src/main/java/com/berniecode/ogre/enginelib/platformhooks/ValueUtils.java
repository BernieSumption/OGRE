package com.berniecode.ogre.enginelib.platformhooks;

import java.util.Arrays;

/**
 * Cross-language operations for working with values
 *
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
//TODO unit tests
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
		if (value1 instanceof Object[] && value2 instanceof Object[]) {
			return arraysAreEquivalent((Object[]) value1, (Object[]) value2);
		}
		return value1.equals(value2);
	}

	private static boolean arraysAreEquivalent(Object[] value1, Object[] value2) {
		if (value1.length != value2.length) {
			return false;
		}
		for (int i=0; i<value1.length; i++) {
			if (!valuesAreEquivalent(value1[i], value2[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Convert a {@link Long} into an object suitable for use as a map key
	 */
	//TODO do I still need this after EntityMap refactor?
	public static Object boxLong(long in) {
		return Long.valueOf(in);
	}

	/**
	 * Perform a shallow copy of an array.
	 * 
	 * @return a new array of the same type as the specified array, containing the same values.
	 */
	public static Object[] cloneArray(Object[] values) {
		return Arrays.copyOf(values, values.length);
	}

}
