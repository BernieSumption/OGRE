package com.berniecode.ogre.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * 
 *
 * @author Bernie Sumption
 */
class EntityFacadeInvocationHandler implements InvocationHandler {
	
	private final Entity entity;
	private final Map<Method, Property> methodMap;

	private EntityFacadeInvocationHandler(Map<Method, Property> methodMap, Entity entity) {
		this.methodMap = methodMap;
		this.entity = entity;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Property property = methodMap.get(method);
		if (property != null) {
			return entity.getPropertyValue(property);
		}
		throw new OgreException("Invocation of non getter method " + method);
	}
	

	private static final Map<Class<?>, Map<Method, Property>> methodMapCache = new HashMap<Class<?>, Map<Method,Property>>();

	public synchronized static EntityFacadeInvocationHandler newInstance(Class<?> klass, Entity entity) {
		Map<Method, Property> methodMap = methodMapCache.get(klass);
		EntityType entityType = entity.getEntityType();
		if (methodMap == null) {
			methodMap = new HashMap<Method, Property>();
			for (Method method: klass.getMethods()) {
				if (Utils.isGetterMethod(method)) {
					String propertyName = Utils.getPropertyNameForGetter(method);
					Property property = entityType.getPropertyByName(propertyName);
					if (property == null) {
						throw new OgreException(entityType + " does not contain a property called '" + propertyName + "'");
					}
					methodMap.put(method, property);
				}
			}
			methodMapCache.put(klass, methodMap);
		}
		return new EntityFacadeInvocationHandler(methodMap, entity);
	}

}
