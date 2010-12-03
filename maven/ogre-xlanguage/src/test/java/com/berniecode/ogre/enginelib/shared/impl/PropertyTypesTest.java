package com.berniecode.ogre.enginelib.shared.impl;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.shared.IntegerPropertyType;

public class PropertyTypesTest extends TestCase {

	public void testTypeCodesAreCorrect() {
		IntegerPropertyType ipt = new IntegerPropertyType(32, false);
		assertEquals(1, ipt.getTypecode());
	}

}
