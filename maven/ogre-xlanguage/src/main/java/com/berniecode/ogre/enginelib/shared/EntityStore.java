package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.Convert;
import com.berniecode.ogre.enginelib.platformhooks.NativeSimpleList;
import com.berniecode.ogre.enginelib.platformhooks.NativeSimpleMap;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

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
		return getEntityMap(entityType).contains(Convert.longToObject(id));
	}

	/**
	 * Check whether this store contains an entity with the same type and id as the specified entity
	 */
	public boolean containsSimilar(Entity entity) {
		return contains(entity.getEntityType(), entity.getId());
	}

	/**
	 * Add an {@link Entity} that does not already exist in the map
	 * 
	 * @throws OgreException if this store already contains an entity with the same name and ID
	 */
	public void addNew(Entity entity) throws OgreException {
		if (contains(entity.getEntityType(), entity.getId())) {
			throw new OgreException("The entity " + entity + " already exists in this store");
		}
		getEntityMap(entity.getEntityType()).put(Convert.longToObject(entity.getId()), entity);
	}
	
	private SimpleMap getEntityMap(EntityType entityType) {
		if (!entities.contains(entityType.getName())) {
			entities.put(entityType.getName(), new NativeSimpleMap());
		}
		return (SimpleMap) entities.get(entityType.getName());
	}

	public Entity[] getAllEntities() {
		SimpleList resultList = new NativeSimpleList();
		Object[] collections = entities.getValues();
		for (int i=0; i<collections.length; i++) {
			SimpleMap collection = (SimpleMap) collections[i];
			resultList.addAll(collection.getValues());
		}
		Entity[] result = new Entity[resultList.size()];
		resultList.copyToArray(result);
		return result;
	}


}
