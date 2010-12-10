package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.ArrayBuilder;
import com.berniecode.ogre.enginelib.platformhooks.NativeSimpleMap;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

/**
 * A big ol' sack full of {@link Entity}s belonging to a single {@link TypeDomain}
 * 
 * @author Bernie Sumption
 */
public class EntityStore {

	// a map of EntityType.name to (map of EntityType.id to Entity) 
	private SimpleMap entities = new NativeSimpleMap();

	/**
	 * Check whether this store contains an entity with a specified type and ID
	 */
	public boolean contains(EntityType entityType, long id) {
		return getEntityMap(entityType).contains(ValueUtils.boxLong(id));
	}

	/**
	 * Check whether this store contains an entity with the same type and id as the specified entity
	 */
	public boolean containsSimilar(Entity entity) {
		return contains(entity.getEntityType(), entity.getId());
	}

	/**
	 * @return a single {@link Entity} from this store specified by type and id, or null if there is
	 *         no such {@link Entity} in the store
	 */
	public Entity get(EntityType entityType, long id) {
		return (Entity) getEntityMap(entityType).get(ValueUtils.boxLong(id));
	}

	/**
	 * @return a single {@link Entity} from this store with the same type and id as the specified
	 *         entity, or null if there is no such {@link Entity} in the store
	 */
	public Entity getSimilar(Entity entity) {
		return get(entity.getEntityType(), entity.getId());
	}

	/**
	 * Add an {@link Entity} that does not already exist in this store.
	 * 
	 * @throws OgreException if this store already contains an entity with the same name and ID
	 */
	//TODO test with existing entity
	public void addNew(Entity entity) throws OgreException {
		if (contains(entity.getEntityType(), entity.getId())) {
			throw new OgreException("The entity " + entity + " already exists in this store");
		}
		replace(entity);
	}

	/**
	 * Add an {@link Entity} to this store, replacing any existing entity with the same type and id
	 * if such an entity exists
	 */
	public void replace(Entity entity) {
		getEntityMap(entity.getEntityType()).put(ValueUtils.boxLong(entity.getId()), entity);
	}

	private SimpleMap getEntityMap(EntityType entityType) {
		if (!entities.contains(entityType.getName())) {
			entities.put(entityType.getName(), new NativeSimpleMap());
		}
		return (SimpleMap) entities.get(entityType.getName());
	}

	public Entity[] getAllEntities() {
		ArrayBuilder resultList = new ArrayBuilder(Entity.class);
		Object[] collections = entities.getValues();
		for (int i=0; i<collections.length; i++) {
			SimpleMap collection = (SimpleMap) collections[i];
			resultList.addAll(collection.getValues());
		}
		return (Entity[]) resultList.buildArray();
	}


}
