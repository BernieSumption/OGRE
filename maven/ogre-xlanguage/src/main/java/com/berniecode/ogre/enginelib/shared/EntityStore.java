package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.ArrayBuilder;
import com.berniecode.ogre.enginelib.platformhooks.EntityMap;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A big ol' sack full of {@link Entity}s belonging to a single {@link TypeDomain}
 * 
 * @author Bernie Sumption
 */
public class EntityStore {
	
	private final TypeDomain typeDomain;
	private final EntityMap[] entityMaps;
	private final boolean allowReplace;
	
	public EntityStore(TypeDomain typeDomain, boolean allowReplace) {
		this.typeDomain = typeDomain;
		this.allowReplace = allowReplace;
		entityMaps = new EntityMap[typeDomain.getEntityTypes().length];
		for (int i=0; i<entityMaps.length; i++) {
			entityMaps[i] = new EntityMap();
		}
	}


	/**
	 * Check whether this store contains an entity with a specified type and ID
	 */
	public boolean contains(EntityType entityType, long id) {
		return entityMaps[entityType.getEntityTypeIndex()].contains(id);
	}

	/**
	 * Check whether this store contains an entity with the same type and id as the specified entity
	 */
	public boolean containsSimilar(EntityReference reference) {
		return contains(reference.getEntityType(), reference.getEntityId());
	}

	/**
	 * @return a single {@link Entity} from this store specified by type and id, or null if there is
	 *         no such {@link Entity} in the store
	 */
	public Entity get(EntityType entityType, long id) {
		return entityMaps[entityType.getEntityTypeIndex()].get(id);
	}

	/**
	 * @return a single {@link Entity} from this store with the same type and id as the specified
	 *         entity, or null if there is no such {@link Entity} in the store
	 */
	public Entity getSimilar(EntityReference reference) {
		return get(reference.getEntityType(), reference.getEntityId());
	}

	/**
	 * Remove an entity from this store
	 */
	public void removeSimilar(EntityReference reference) {
		int entityTypeIndex = reference.getEntityType().getEntityTypeIndex();
		entityMaps[entityTypeIndex].remove(reference.getEntityId());
	}

	/**
	 * Add an {@link Entity} to the store.
	 * 
	 * @throws OgreException if this store already contains an entity with the same type and ID and
	 *             this store does not allow replacement of existing entities
	 */
	public void put(Entity entity) throws OgreException {
		if (!allowReplace && containsSimilar(entity)) {
			throw new OgreException("The entity " + getSimilar(entity) + " already exists in this store");
		}
		entityMaps[entity.getEntityTypeIndex()].put(entity);
	}

	/**
	 * Add many entities to this store
	 * 
	 * @throws OgreException if this store already contains an Entity with the same type and ID as
	 *             any of the specified entieies, and this store does not allow replacement of
	 *             existing entities
	 */
	public void putAll(Entity[] entities) throws OgreException {
		for (int i = 0; i < entities.length; i++) {
			put(entities[i]);
		}
	}


	/**
	 * @return every {@link Entity} in this store
	 */
	public Entity[] getEntities() {
		ArrayBuilder resultList = new ArrayBuilder(Entity.class);
		for (int i=0; i<entityMaps.length; i++) {
			resultList.addAll(entityMaps[i].getEntities());
		}
		return (Entity[]) resultList.buildArray();
	}


}
