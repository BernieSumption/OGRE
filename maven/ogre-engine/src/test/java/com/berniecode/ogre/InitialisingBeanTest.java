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

import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;

import junit.framework.TestCase;

/**
 * Support class for creating beans that have dependencies that must be set before the class is
 * initialised, and can't be set again after initialisation.
 * 
 * @author Bernie Sumption
 */
public class InitialisingBeanTest extends TestCase {

	public void testDependenciesRequired() {
		try {
			new MyInitialisingBean().initialise();
			fail("Should have failed with InitialisationException");
		} catch (InitialisationException e) {
		}
	}

	public void testInitialisationHappensOnce() {
		MyInitialisingBean bean = new MyInitialisingBean();
		bean.setDep1(new Object());
		bean.initialise();
		bean.initialise();
		assertEquals(1, bean.initCount);
	}

	public void testRequireInitialised() {
		MyInitialisingBean bean = new MyInitialisingBean();
		try {
			bean.requireInitialisation();
			fail("Should have failed with InitialisationException");
		} catch (InitialisationException e) {
		}

		bean = new MyInitialisingBean();
		bean.setDep1(new Object());
		bean.initialise();
		try {
			bean.setDep1(new Object());
			fail("Should have failed with InitialisationException");
		} catch (InitialisationException e) {
		}
	}

}

class MyInitialisingBean extends InitialisingBean {

	public int initCount;

	private Object dep1;

	@Override
	protected void doInitialise() {
		requireNotNull(dep1, "dep1");
		initCount++;
	}

	public void setDep1(Object dep1) {
		requireInitialised(false, "setDep1()");
		this.dep1 = dep1;
	}

	public void requireInitialisation() {
		requireInitialised(true, "requiredInitialisation()");
	}

}
