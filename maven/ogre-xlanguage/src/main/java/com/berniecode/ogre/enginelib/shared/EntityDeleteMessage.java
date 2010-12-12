package com.berniecode.ogre.enginelib.shared;


/**
 * Represents the removal of an {@link Entity} from an object graph
 *
 * @author Bernie Sumption
 */
public class EntityDeleteMessage implements EntityReference {

	private final int entityTypeIndex;
	private final long entityId;

	public EntityDeleteMessage(int entityTypeIndex, long id) {
		this.entityTypeIndex = entityTypeIndex;
		this.entityId = id;
	}

	/**
	 * @return An {@link EntityDeleteMessage} object tfr the specified entity
	 */
	public static EntityDeleteMessage build(Entity entity) {
		return new EntityDeleteMessage(entity.getEntityTypeIndex(), entity.getEntityId());
	}

	/**
	 * @return The type of this Entity
	 */
	public int getEntityTypeIndex() {
		return entityTypeIndex;
	}

	/**
	 * @return The ID of this entity, unique within the scope of its entity type
	 */
	public long getEntityId() {
		return entityId;
	}

	public String toString() {
		return "EntityDeleteMessage for entity (entityTypeIndex=" + entityTypeIndex + ", entityId=" + entityId + ")";
	}

}
