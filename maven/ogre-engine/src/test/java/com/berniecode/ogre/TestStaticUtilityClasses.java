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

package com.berniecode.ogre;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

public class TestStaticUtilityClasses extends TestCase {

	public void test() throws Exception {
		doTestNoArgPrivateConstructor(EDRDescriber.class);
		doTestNoArgPrivateConstructor(Utils.class);
		doTestNoArgPrivateConstructor(OgreLog.class);
		doTestNoArgPrivateConstructor(ValueUtils.class);
	}

	private void doTestNoArgPrivateConstructor(Class<?> klass) throws Exception {

		assertEquals(1, klass.getDeclaredConstructors().length);

		Constructor<?> c = klass.getDeclaredConstructor();
		assertTrue("Constructor must be private", Modifier.isPrivate(c.getModifiers()));
		// call constructor for 100% test coverage. OCD I know.
		c.setAccessible(true);
		c.newInstance();
	}

}
