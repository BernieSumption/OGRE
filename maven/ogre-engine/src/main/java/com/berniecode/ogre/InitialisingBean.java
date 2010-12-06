package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;


/**
 * Support class for creating beans that have dependencies that must be set before the class is
 * initialised, and can't be set again after initialisation.
 * 
 * @author Bernie Sumption
 */
public abstract class InitialisingBean {

	private boolean initialised;

	/**
	 * Initialise this bean if it has not already been initialised.
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
			throw new InitialisationException("A value for " + name + " must be supplied before initialise() is called.");
		}
	}

	/**
	 * Check that the bean has been initialised (for use in methods that require dependencies) or
	 * has not been initialised (for use in dependency setter methods)
	 */
	protected void requireInitialised(boolean requiredStatus, String methodName) {
		if (initialised != requiredStatus) {
			throw new InitialisationException(methodName + " can't be called " + (requiredStatus ? "before" : "after")
					+ " initialise()");
		}
	}

}
