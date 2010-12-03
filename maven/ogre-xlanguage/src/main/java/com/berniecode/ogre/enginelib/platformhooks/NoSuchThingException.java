package com.berniecode.ogre.enginelib.platformhooks;

/**
 * An Exception thrown when a lookup of an object by ID failed (e.g. requesting a TypeDomain by
 * string ID or requesting a Property by integer index)
 * 
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class NoSuchThingException extends RuntimeException {

	public NoSuchThingException(String message) {
		super(message);
	}
}
