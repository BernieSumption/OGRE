package com.berniecode.ogre.enginelib.platformhooks;

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

		assertTrue(ValueUtils.valuesAreEquivalent(new byte[] {1, 3, 5}, new byte[] {1, 3, 5}));
	}

}
