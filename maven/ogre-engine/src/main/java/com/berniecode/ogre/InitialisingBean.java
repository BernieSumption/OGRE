package com.berniecode.ogre;

import com.berniecode.ogre.engine.platformhooks.OgreException;

/**
 * Support class for creating beans that have dependencies that must be set before the class is
 * initialised, and cn't be set again after initialisation.
 * 
 * @author Bernie Sumption
 */
public abstract class InitialisingBean {

	private boolean initialised;

	/**
	 * Check whether this bean is initialised
	 */
	public boolean isInitialised() {
		return initialised;
	}

	/**
	 * Initialise this bean.
	 * 
	 * @throws OgreException if the required dependencies have not been set yet
	 */
	public void initialise() throws OgreException {
		if (!initialised) {
			initialised = true;
			doInitialise();
		}
	}

	/**
	 * This method should be overridden to contain calls to requireNotNull(property, "property")
	 */
	protected abstract void doInitialise();

	/**
	 * Check that a dependency has been provided
	 */
	protected void requireNotNull(Object required, String name) {
		if (required == null) {
			throw new OgreException("A value for " + name + " must be supplied before initialise() is called.");
		}
	}

	/**
	 * Check that the bean has been initialised (for use in methods that require dependencies) or
	 * has not been initialised (for use in dependency setter methods)
	 */
	protected void requireInitialised(boolean requiredStatus, String methodName) {
		if (initialised != requiredStatus) {
			throw new OgreException(methodName + " can't be called " + (requiredStatus ? "before" : "after")
					+ " initialise()");
		}
	}

}
