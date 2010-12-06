package com.berniecode.ogre.enginelib.platformhooks;

/**
 * Thrown when an object has been incorrectly constructed and initialised
 *
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class InitialisationException extends RuntimeException {

	public InitialisationException(String message) {
		super(message);
	}

}
