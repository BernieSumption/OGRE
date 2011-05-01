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

package com.berniecode.ogre.enginelib.platformhooks;

import java.util.HashMap;
import java.util.Map;

import com.berniecode.ogre.enginelib.Entity;

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
	 * @return all {@link Entity}s in this map. The returned list is a copy of the internal list,
	 *         and is safe to modify.
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
