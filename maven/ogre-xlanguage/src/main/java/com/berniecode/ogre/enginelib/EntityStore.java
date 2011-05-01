/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.ArrayBuilder;
import com.berniecode.ogre.enginelib.platformhooks.EntityMap;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A big ol' sack full of {@link Entity}s belonging to a single {@link TypeDomain}
 * 
 * @author Bernie Sumption
 */
public class EntityStore {
	
	private final EntityMap[] entityMaps;
	
	public EntityStore(TypeDomain typeDomain) {
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
	 *         {@link EntityReference}, or null if there is no such {@link Entity} in the store
	 */
	public Entity getSimilar(EntityReference reference) {
		return get(reference.getEntityType(), reference.getEntityId());
	}

	/**
	 * Remove an entity from this store. This method has no effect if no such entity exists.
	 */
	public void remove(EntityType entityType, long id) {
		Entity entityToRemove = get(entityType, id);
		if (entityToRemove != null) {
			entityMaps[entityType.getEntityTypeIndex()].remove(id);
			
			// null all references to the removed entity
			for (int i = 0; i < entityMaps.length; i++) {
				Entity[] entities = entityMaps[i].getEntities();
				if (entities.length > 0 && entities[0].getEntityType().isReferenceTo(entityToRemove.getEntityType())) {
					for (int j = 0; j < entities.length; j++) {
						entities[j].nullReferencesTo(entityToRemove);
					}
				}
			}
		}
	}

	/**
	 * Remove an entity from this store with the same type and id as the specified
	 * {@link EntityReference}. This method has no effect if no such entity exists.
	 */
	public void removeSimilar(EntityReference reference) {
		remove(reference.getEntityType(), reference.getEntityId());
	}

	/**
	 * Add an {@link Entity} to the store.
	 * 
	 * @throws OgreException if this store already contains an entity with the same type and ID and
	 *             this store does not allow replacement of existing entities
	 */
	public void add(Entity entity) throws OgreException {
		if (containsSimilar(entity)) {
			throw new OgreException("The entity " + getSimilar(entity) + " already exists in this store");
		}
		entityMaps[entity.getEntityType().getEntityTypeIndex()].put(entity);
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
			add(entities[i]);
		}
	}


	/**
	 * @return every {@link Entity} in this store. The returned list is a copy of the internal list,
	 *         and is safe to modify.
	 */
	public Entity[] getEntities() {
		ArrayBuilder resultList = new ArrayBuilder(Entity.class);
		for (int i=0; i<entityMaps.length; i++) {
			resultList.addAll(entityMaps[i].getEntities());
		}
		return (Entity[]) resultList.buildArray();
	}

	/**
	 * @return every {@link Entity} of the specified {@link EntityType} in this store. The returned
	 *         list is a copy of the internal list, and is safe to modify.
	 */
	public Entity[] getEntitiesByType(EntityType entityType) {
		return entityMaps[entityType.getEntityTypeIndex()].getEntities();
	}


}
