package com.berniecode.ogre;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.platformhooks.OgreLog;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;

public class TestStaticUtilityClasses extends TestCase {
	
	public void test() throws Exception {
		doTestNoArgPrivateConstructor(EDRDescriber.class);
		doTestNoArgPrivateConstructor(Utils.class);
		doTestNoArgPrivateConstructor(OgreLog.class);
	}
	
	
	private void doTestNoArgPrivateConstructor(Class<?> klass) throws Exception {
		
		assertEquals(1, klass.getDeclaredConstructors().length);
		
		Constructor<?> c = klass.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(c.getModifiers()));
		// call constructor for 100% test coverage. OCD I know.
		c.setAccessible(true);
		c.newInstance();
	}

}