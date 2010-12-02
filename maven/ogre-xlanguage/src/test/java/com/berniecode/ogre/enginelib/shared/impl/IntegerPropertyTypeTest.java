package com.berniecode.ogre.enginelib.shared.impl;

import com.berniecode.ogre.enginelib.shared.impl.IntegerPropertyType;

import junit.framework.TestCase;

public class IntegerPropertyTypeTest extends TestCase {

	public void testNameIsCorrect() {
		IntegerPropertyType ipt = new IntegerPropertyType();
		assertEquals("integer", ipt.getName());
	}

}
