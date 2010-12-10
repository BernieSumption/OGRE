package com.berniecode.ogre.enginelib.shared;

/**
 * A set of values for an {@link Entity}, one for each {@link Property}
 * 
 * <p>
 * Unlike {@link Entity}, this is a simple value object suitable for serialisation and transmission
 * over a network
 * 
 * @author Bernie Sumption
 */
public class EntityValueMessage implements EntityReference, EntityUpdate {

	private final int entityTypeIndex;
	private final long entityId;
	private final Object[] values;
	
	public EntityValueMessage(int entityIndex, long entityId, Object[] values) {
		this.entityTypeIndex = entityIndex;
		this.entityId = entityId;
		this.values = values;
	}

	public int getEntityTypeIndex() {
		return entityTypeIndex;
	}

	public long getEntityId() {
		return entityId;
	}

	public Object getValue(int index) {
		return values[index];
	}

	/**
	 * Create an {@link EntityValueMessage} from an {@link Entity}
	 */
	public static EntityValueMessage build(Entity entity) {
		return new EntityValueMessage(entity.getEntityTypeIndex(), entity.getEntityId(), entity.copyValues());
	}

	/**
	 * @return an {@link Entity} with the same type, id and data as this {@link EntityValueMessage}
	 */
	public Entity toEntity(TypeDomain typeDomain) {
		return new Entity(typeDomain.getEntityType(entityTypeIndex), entityId, values);
	}

	/**
	 * @see EntityUpdate#hasUpdatedValue(int)
	 */
	public boolean hasUpdatedValue(int propertyIndex) {
		return true; // an EntityValueMessage updates every property
	}

}
