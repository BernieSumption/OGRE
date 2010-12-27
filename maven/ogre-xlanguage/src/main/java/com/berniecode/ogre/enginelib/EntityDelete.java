package com.berniecode.ogre.enginelib;



/**
 * Represents the removal of an {@link Entity} from an object graph
 *
 * @author Bernie Sumption
 */
public class EntityDelete implements EntityReference {

	private final EntityType entityType;
	private final long entityId;

	public EntityDelete(EntityType entityType, long id) {
		this.entityType = entityType;
		this.entityId = id;
	}

	/**
	 * @return An {@link EntityDelete} object tfr the specified entity
	 */
	public static EntityDelete build(Entity entity) {
		return new EntityDelete(entity.getEntityType(), entity.getEntityId());
	}

	/**
	 * @return The type of this Entity
	 */
	public EntityType getEntityType() {
		return entityType;
	}

	/**
	 * @return The ID of this entity, unique within the scope of its entity type
	 */
	public long getEntityId() {
		return entityId;
	}

	public String toString() {
		return "EntityDelete for entity " + entityType + "#" + entityId;
	}

}
