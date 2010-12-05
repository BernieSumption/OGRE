package com.berniecode.ogre;

/**
 * Thrown when an {@link InitialisingBean} has been incorrectly constructed and initialised
 *
 * @author Bernie Sumption
 */
public class InitialisationException extends RuntimeException {

	public InitialisationException(String message) {
		super(message);
	}

}
