package com.berniecode.ogre.enginelib.platformhooks;

import java.util.Date;

import junit.framework.TestCase;

/**
 * Cross-language operations for working with values
 *
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class ValueUtilsTest extends TestCase {

	public void testValuesAreEquivalent() {

		assertTrue(ValueUtils.valuesAreEquivalent(2, 2));
		assertTrue(ValueUtils.valuesAreEquivalent(null, null));
		assertFalse(ValueUtils.valuesAreEquivalent(1, null));
		assertFalse(ValueUtils.valuesAreEquivalent(null, 2));
		assertFalse(ValueUtils.valuesAreEquivalent(1, 2));

		assertTrue(ValueUtils.valuesAreEquivalent(new Object[] {1, 3, 5}, new Object[] {1, 3, 5}));
		assertFalse(ValueUtils.valuesAreEquivalent(new Object[] {1, 3, 5}, "abc"));
		assertFalse(ValueUtils.valuesAreEquivalent(new Date(), new Object[] {1, 3, 5}));
		assertFalse(ValueUtils.valuesAreEquivalent(new Object[] {1, 3, 5}, new Object[] {1, 3}));
		assertFalse(ValueUtils.valuesAreEquivalent(new Object[] {1, 3, 5}, new Object[] {1, 5, 5}));
	}

	public void testCloneArray(Object[] values) {
		Object[] a = new Object[] {10, 3, 42, 118, 928374, Long.MAX_VALUE};
		Object[] b = ValueUtils.cloneArray(a);
		assertFalse(a == b);
		assertTrue(ValueUtils.valuesAreEquivalent(a, b));
	}

}
