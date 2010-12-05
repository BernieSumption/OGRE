package com.berniecode.ogre.server.pojods;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * Exception thrown when {@link PojoDataSource} can't map a Java type onto an OGRE tyoe, or vice
 * versa
 * 
 * @author Bernie Sumption
 */
public class TypeMappingException extends OgreException {

	public TypeMappingException(String message) {
		super(message);
	}

}
