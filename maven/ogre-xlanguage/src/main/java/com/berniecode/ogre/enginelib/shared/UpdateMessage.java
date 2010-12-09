package com.berniecode.ogre.enginelib.shared;


/**
 * Represents a change to an object graph.
 * 
 * @author Bernie Sumption
 */
public class UpdateMessage {

	private final String typeDomainId;
	private final String objectGraphId;
	private final Entity[] entities;

	public UpdateMessage(String typeDomainId, String objectGraphId, Entity[] entities) {
		this.typeDomainId = typeDomainId;
		this.objectGraphId = objectGraphId;
		this.entities = entities;
	}

	/**
	 * Together with {@link #getObjectGraphId()}, identifies the object graph that this message
	 * should be applied to
	 */
	public String getTypeDomainId() {
		return typeDomainId;
	}

	/**
	 * Together with {@link #getTypeDomainId()}, identifies the object graph that this message
	 * should be applied to
	 */
	public String getObjectGraphId() {
		return objectGraphId;
	}

	/**
	 * @return {@link Entity}s that have been created or updated.
	 */
	public Entity[] getCompleteEntities() {
		return entities;
	}

}
