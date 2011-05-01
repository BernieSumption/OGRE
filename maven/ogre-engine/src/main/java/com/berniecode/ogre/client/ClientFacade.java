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

package com.berniecode.ogre.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.UnsafeAccess;

/**
 * A wrapper around a {@link ClientEngine} that transparently converts between OGRE's
 * <em>Entity Data Representation</em> and Java objects.
 * 
 * <p>
 * Using a ClientFacade instead of {@link ClientEngine} provides a more natural interface to an
 * object graph.
 * 
 * @author Bernie Sumption
 */
public class ClientFacade {

	private final ClientEngine clientEngine;
	private final EDRFacadeFactory mapper;

	private final Map<Class<?>, EntityType> classToType = new HashMap<Class<?>, EntityType>();
	private final Map<Entity, Object> entityToObject = new WeakHashMap<Entity, Object>();

	/**
	 * Construct a ClientFacade with a {@link DefaultEDRFacadeFactory}.
	 */
	public ClientFacade(ClientEngine clientEngine) {
		this(clientEngine, new DefaultEDRFacadeFactory(clientEngine));
	}

	/**
	 * Create a facade for a {@link ClientEngine} instance using a custom {@link EDRFacadeFactory}.
	 * 
	 * @see ClientFacade
	 */
	public ClientFacade(ClientEngine clientEngine, EDRFacadeFactory mapper) {
		this.clientEngine = clientEngine;
		this.mapper = mapper;
		for (EntityType entityType : UnsafeAccess.getEntityTypes(clientEngine.getTypeDomain())) {
			classToType.put(mapper.getClassForEntityType(entityType), entityType);
		}
	}

	/**
	 * @return a list of classes that can be passed to {@link #getEntitiesByType(Class)}
	 */
	public Collection<Class<?>> getEntityClasses() {
		return Collections.unmodifiableSet(classToType.keySet());
	}

	/**
	 * @return all the instances of the specified type currently in the object graph
	 */
	public <T> List<T> getEntitiesByType(Class<T> entityClass) {
		EntityType entityType = getClassForEntityType(entityClass);
		List<T> result = new ArrayList<T>();
		for (Entity entity : clientEngine.getEntitiesByType(entityType)) {
			result.add(getFacadeForEntity(entityClass, entity));
		}
		return result;
	}

	/**
	 * @return a specific instance by type and id
	 */
	public <T> T getEntity(Class<T> entityClass, long id) {
		EntityType entityType = getClassForEntityType(entityClass);
		Entity entity = clientEngine.getEntityByTypeAndId(entityType, id);
		return getFacadeForEntity(entityClass, entity);
	}

	//
	// PRIVATE MACHINERY
	//

	private <T> EntityType getClassForEntityType(Class<T> entityClass) {
		EntityType entityType = classToType.get(entityClass);
		if (entityType == null) {
			throw new ClientFacadeException(entityClass + " does not map onto an entity managed by " + clientEngine);
		}
		return entityType;
	}

	private <T> T getFacadeForEntity(Class<T> entityClass, Entity entity) {
		T object = entityClass.cast(entityToObject.get(entity));
		if (object != null) {
			return object;
		}
		object = entityClass.cast(mapper.getFacadeForEntity(entity));
		entityToObject.put(entity, object);
		return object;
	}

}
