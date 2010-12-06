package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.Convert;
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
	
	private int count;

	public boolean contains(EntityType entityType, long id) {
		return getEntityMap(entityType).contains(Convert.longToObject(id));
	}

	/**
	 * Add an {@link Entity} that does not already exist in the map
	 */
	public void putNew(Entity entity) throws OgreException {
		if (contains(entity.getEntityType(), entity.getId())) {
			throw new OgreException("The entity " + entity + " already exists in this store");
		}
		getEntityMap(entity.getEntityType()).put(Convert.longToObject(entity.getId()), entity);
		count++;
	}
	
	private SimpleMap getEntityMap(EntityType entityType) {
		if (!entities.contains(entityType.getName())) {
			entities.put(entityType.getName(), new NativeSimpleMap());
		}
		return (SimpleMap) entities.get(entityType.getName());
	}

	public Entity[] getAllEntities() {
		Entity[] result = new Entity[count];
		Object[] collections = entities.getValues();
		int index = 0;
		for (int i=0; i<collections.length; i++) {
			SimpleMap collection = (SimpleMap) collections[i];
			Object[] entities = collection.getValues();
			for (int j=0; j<entities.length; j++) {
				result[index] = (Entity) entities[j];
				index++;
			}
		}
		return result;
	}


}
