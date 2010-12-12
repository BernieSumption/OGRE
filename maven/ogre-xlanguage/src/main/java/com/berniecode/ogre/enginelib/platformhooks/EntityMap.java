package com.berniecode.ogre.enginelib.platformhooks;

import java.util.HashMap;
import java.util.Map;

import com.berniecode.ogre.enginelib.shared.Entity;

/**
 * A collection of {@link Entity}s of a single entity type, indexed by ID for quick access
 *
 * @author Bernie Sumption
 */
public class EntityMap {
	
	private Map entities = new HashMap();

	/**
	 * Check if this map contains an entity of the specified ID
	 */
	public boolean contains(long id) {
		return entities.containsKey(Long.valueOf(id));
	}

	/**
	 * Store an entity in this map
	 */
	public void put(Entity entity) {
		entities.put(Long.valueOf(entity.getEntityId()), entity);
	}

	/**
	 * @return all {@link Entity}s in this map
	 */
	public Entity[] getEntities() {
		return (Entity[]) entities.values().toArray(new Entity[0]);
	}

	/**
	 * @return the {@link Entity} with the specified ID, or null if no such {@link Entity} exists in
	 *         this map
	 */
	public Entity get(long id) {
		return (Entity) entities.get(Long.valueOf(id));
	}

	/**
	 * Remove an entity form this map
	 */
	public void remove(long id) {
		entities.remove(Long.valueOf(id));
	}

}
