package com.berniecode.ogre.client;

import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityType;

/**
 * Maps from OGRE's Entity Data representation to Java classes and objects
 *
 * @author Bernie Sumption
 */
public interface EDRFacadeFactory {

	/**
	 * Return a class or interface suitable for accessing entities of the specified
	 * {@link EntityType}.
	 */
	Class<?> getClassForEntityType(EntityType entityType);

	/**
	 * Return a facade for an {@link Entity}. The returned object will be an instance of the
	 * type returned by {@code getClassForEntityType(entity.getEntityType())}
	 */
	Object getFacadeForEntity(Entity entity) throws ClientFacadeException;
}
