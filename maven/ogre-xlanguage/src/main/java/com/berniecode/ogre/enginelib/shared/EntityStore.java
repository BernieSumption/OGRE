package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.ArrayBuilder;
import com.berniecode.ogre.enginelib.platformhooks.EntityMap;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A big ol' sack full of {@link Entity}s belonging to a single {@link TypeDomain}
 * 
 * @author Bernie Sumption
 */
//TODO serious clean-up of public API
public class EntityStore {
	
	private final TypeDomain typeDomain;
	
	private final EntityMap[] entityMaps;
	
	public EntityStore(TypeDomain typeDomain) {
		this.typeDomain = typeDomain;
		entityMaps = new EntityMap[typeDomain.getEntityTypes().length];
		for (int i=0; i<entityMaps.length; i++) {
			entityMaps[i] = new EntityMap();
		}
	}


	/**
	 * Check whether this store contains an entity with a specified type and ID
	 */
	public boolean contains(int entityTypeIndex, long id) {
		return entityMaps[entityTypeIndex].contains(id);
	}

	/**
	 * Check whether this store contains an entity with the same type and id as the specified entity
	 */
	public boolean containsSimilar(EntityReference entityValue) {
		return contains(entityValue.getEntityTypeIndex(), entityValue.getEntityId());
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
		return get(typeDomain.getEntityType(reference.getEntityTypeIndex()), reference.getEntityId());
	}
	
	public void replace(Entity entity) {
		entityMaps[entity.getEntityTypeIndex()].put(entity);
	}

	/**
	 * Add an {@link Entity} that does not already exist in this store.
	 * 
	 * @throws OgreException if this store already contains an entity with the same name and ID
	 */
	//TODO test with existing entity
	public void addNew(Entity entity) throws OgreException {
		Entity existing = getSimilar(entity);
		if (existing != null) {
			throw new OgreException("The entity " + existing + " already exists in this store");
		}
		entityMaps[entity.getEntityTypeIndex()].put(entity);
	}

	public Entity[] getAllEntities() {
		ArrayBuilder resultList = new ArrayBuilder(Entity.class);
		for (int i=0; i<entityMaps.length; i++) {
			resultList.addAll(entityMaps[i].getEntities());
		}
		return (Entity[]) resultList.buildArray();
	}


}
