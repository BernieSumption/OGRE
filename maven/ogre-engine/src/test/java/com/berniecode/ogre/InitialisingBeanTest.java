package com.berniecode.ogre;

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
