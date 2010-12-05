package com.berniecode.ogre.server.pojods;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * Exception thrown when {@link PojoDataSource} can't get an entity property value from a java
 * object.
 * 
 * @author Bernie Sumption
 */
public class ValueMappingException extends OgreException {

	public ValueMappingException(String message) {
		super(message);
	}

	public ValueMappingException(String message, Throwable cause) {
		super(message, cause);
	}

}
