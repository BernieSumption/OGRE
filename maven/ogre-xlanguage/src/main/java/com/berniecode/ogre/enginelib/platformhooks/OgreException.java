package com.berniecode.ogre.enginelib.platformhooks;

/**
 * An error in the OGRE system
 * 
 * @author Bernie Sumption
 */
public class OgreException extends RuntimeException {

	public OgreException(String message) {
		super(message);
	}

	public OgreException(String message, Throwable cause) {
		super(message, cause);
	}

}
