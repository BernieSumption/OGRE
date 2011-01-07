package com.berniecode.ogre.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.UnsafeAccess;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

class ClientFacadeInvocationHandler implements InvocationHandler {

	private final ClientEngine clientEngine;
	private final EDRToJavaMapper mapper;

	private final Method getEntityClasses;
	private final Method getEntitiesByType;

	private final Map<Class<?>, EntityType> classToType = new HashMap<Class<?>, EntityType>();
	private final Map<Entity, Object> entityToObject = new WeakHashMap<Entity, Object>();


	public ClientFacadeInvocationHandler(Class<? extends ClientFacade> klass, ClientEngine clientEngine, EDRToJavaMapper mapper) {
		this.clientEngine = clientEngine;
		this.mapper = mapper;
		
		try {
			getEntityClasses = klass.getMethod("getEntityClasses");
			getEntitiesByType = klass.getMethod("getEntitiesByType", Class.class);
		} catch (Exception e) {
			throw new OgreException("Error building ClientFacade", e);
		}
		
		for (EntityType entityType: UnsafeAccess.getEntityTypes(clientEngine.getTypeDomain())) {
			classToType.put(mapper.getClassForEntityType(entityType), entityType);
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method == getEntityClasses) {
			return Collections.unmodifiableSet(classToType.keySet());
		}
		if (method == getEntitiesByType) {
			Class<?> entityClass = (Class<?>) args[0];
			EntityType entityType = classToType.get(entityClass);
			Collection<Object> result = new ArrayList<Object>();
			for (Entity entity: clientEngine.getEntitiesByType(entityType)) {
				result.add(getFacadeForEntity(entityClass, entity));
			}
			return result;
		}
		throw new OgreException("");
	}

	private Object getFacadeForEntity(Class<?> entityClass, Entity entity) {
		Object object = entityToObject.get(entity);
		if (object != null) {
			return object;
		}
		// cast now to fail fast in the event of an implementation that breaks the EDRToJavaMapper contract
		object = entityClass.cast(mapper.getFacadeForEntity(entityClass, entity));
		entityToObject.put(entity, object);
		return object;
	}

}
