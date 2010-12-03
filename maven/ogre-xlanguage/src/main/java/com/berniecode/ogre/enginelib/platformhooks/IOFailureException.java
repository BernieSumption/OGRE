package com.berniecode.ogre.enginelib.platformhooks;

/**
 * An Exception thrown when an IO error occurs (we can't use java.lang.IOException because this is a
 * cross-language library).
 * 
 * <p>
 * This exception is usually temporary, and it is appropriate to retry the action later
 * 
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class IOFailureException extends Exception {

	public IOFailureException(String message) {
		super(message);
	}
}