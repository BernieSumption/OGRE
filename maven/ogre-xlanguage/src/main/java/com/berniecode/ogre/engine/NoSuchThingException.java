package com.berniecode.ogre.engine;

/**
 * An Exception thrown when a lookup of an object by ID failed (e.g. requesting a TypeDomain by
 * string ID or requesting a Property by integer index)
 * 
 * @author Bernie Sumption
 */
public class NoSuchThingException extends RuntimeException {

	public NoSuchThingException(String message) {
		super(message);
	}
}
