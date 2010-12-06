package com.berniecode.ogre.enginelib.shared;

/**
 * Represents a change to an object graph.
 * 
 * @author Bernie Sumption
 */
public interface UpdateMessage {

	/**
	 * @return {@link Entity}s that have been created or updated.
	 */
	Entity[] getCompleteEntities();

}
