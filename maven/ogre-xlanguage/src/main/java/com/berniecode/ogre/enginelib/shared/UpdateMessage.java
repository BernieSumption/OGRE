package com.berniecode.ogre.enginelib.shared;

/**
 * Represents a change to an object graph.
 * 
 * @author Bernie Sumption
 */
public interface UpdateMessage {
	
	/**
	 * Together with {@link #getObjectGraphId()}, identifies the object graph that this message
	 * should be applied to
	 */
	String getTypeDomainId();

	/**
	 * Together with {@link #getTypeDomainId()}, identifies the object graph that this message
	 * should be applied to
	 */
	String getObjectGraphId();

	/**
	 * @return {@link Entity}s that have been created or updated. This array is not safe to modify,
	 *         and should be copied before being passed outside of OGRE.
	 */
	Entity[] getCompleteEntities();

}
