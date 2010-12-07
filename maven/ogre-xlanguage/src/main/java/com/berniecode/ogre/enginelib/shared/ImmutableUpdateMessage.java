package com.berniecode.ogre.enginelib.shared;

/**
 * An implementation of {@link UpdateMessage} that takes all of its values in the constructor
 *
 * @author Bernie Sumption
 */
public class ImmutableUpdateMessage implements UpdateMessage {

	private final String typeDomainId;
	private final String objectGraphId;
	private final Entity[] entities;

	public ImmutableUpdateMessage(String typeDomainId, String objectGraphId, Entity[] entities) {
		this.typeDomainId = typeDomainId;
		this.objectGraphId = objectGraphId;
		this.entities = entities;
	}

	public String getTypeDomainId() {
		return typeDomainId;
	}

	public String getObjectGraphId() {
		return objectGraphId;
	}

	public Entity[] getCompleteEntities() {
		return entities;
	}

}
