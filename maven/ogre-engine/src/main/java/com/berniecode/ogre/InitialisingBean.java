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
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * Support class for creating beans that have dependencies that must be set before the class is
 * initialised, and can't be set again after initialisation.
 * 
 * @author Bernie Sumption
 */
// TODO ensure that all subclasses of this provide a no-arg constructor and a constructor that
// provides all arguments and calls initialise()
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
			throw new InitialisationException("A value for " + name
					+ " must be supplied before initialise() is called.");
		}
	}

	/**
	 * Check that the bean has been initialised (for use in methods that require dependencies) or
	 * has not been initialised (for use in dependency setter methods)
	 */
	protected void requireInitialised(boolean requiredStatus, String methodName) {
		if (initialised != requiredStatus) {
			throw new InitialisationException(getClass().getSimpleName() + "." + methodName + " can't be called "
					+ (requiredStatus ? "before" : "after") + " initialise()");
		}
	}

}
