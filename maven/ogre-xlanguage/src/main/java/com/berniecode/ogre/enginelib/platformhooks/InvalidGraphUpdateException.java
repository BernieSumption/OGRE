package com.berniecode.ogre.enginelib.platformhooks;

/**
 * An Exception thrown when a lookup of an object by ID failed (e.g. requesting a TypeDomain by
 * string ID or requesting a Property by integer index)
 * 
 * @author Bernie Sumption
 */
public class InvalidGraphUpdateException extends OgreException {

	public InvalidGraphUpdateException(String message) {
		super(message);
	}
}
