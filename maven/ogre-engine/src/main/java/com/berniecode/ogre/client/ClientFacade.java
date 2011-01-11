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
		EntityType entityType = classToType.get(entityClass);
		List<T> result = new ArrayList<T>();
		for (Entity entity : clientEngine.getEntitiesByType(entityType)) {
			result.add(getFacadeForEntity(entityClass, entity));
		}
		return result;
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
