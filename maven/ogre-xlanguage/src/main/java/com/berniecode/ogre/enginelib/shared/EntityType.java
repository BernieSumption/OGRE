package com.berniecode.ogre.enginelib.shared;

/**
 * A description of an entity. EntityType is to {@link Entity} as java.lang.Class is to
 * java.lang.Object.
 * 
 * @author Bernie Sumption
 */
public interface EntityType extends Named {

	/**
	 * @return The name of this entity type, typically a fully qualified class name.
	 */
	String getName();

	/**
	 * @return The {@link Property}s of this entity type. The returned array is not safe to modify.
	 *         It must be copied before being passed outside of OGRE
	 */
	Property[] getProperties();
}
