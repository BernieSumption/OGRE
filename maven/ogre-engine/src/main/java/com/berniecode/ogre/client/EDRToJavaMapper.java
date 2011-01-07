package com.berniecode.ogre.client;

import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityType;

/**
 * Maps from OGRE's Entity Data representation to Java classes and objects
 *
 * @author Bernie Sumption
 */
public interface EDRToJavaMapper {

	/**
	 * Return a class or interface suitable for accessing entities of the specified
	 * {@link EntityType}.
	 */
	Class<?> getClassForEntityType(EntityType entityType);

	/**
	 * Return a facade for an {@link Entity}.
	 * 
	 * <p>
	 * The returned object must be an instance of the class returned by
	 * {@link #getClassForEntityType(EntityType)} when called with the specified {@link Entity}'s
	 * {@link EntityType}
	 */
	<T> T getFacadeForEntity(Class<T> klass, Entity entity);
}
