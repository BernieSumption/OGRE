package com.berniecode.ogre.engine.shared.impl;

import junit.framework.TestCase;

public class IntegerPropertyTypeTest extends TestCase {

	public void testNameIsCorrect() {
		IntegerPropertyType ipt = new IntegerPropertyType();
		assertEquals("integer", ipt.getName());
	}

}
