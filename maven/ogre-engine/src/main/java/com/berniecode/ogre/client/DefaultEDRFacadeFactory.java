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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.UnsafeAccess;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;

/**
 * This implementation of {@link EDRFacadeFactory} uses {@link Proxy} instances to provide a
 * strongly typed facade for {@link Entity} objects.
 * 
 * <p>
 * This class requires an interface for each {@link EntityType} in the {@link ClientEngine}'s
 * {@link TypeDomain}. This mapping can be provided explicitly through the constructor, or
 * implicitly based on the {@link EntityType}'s name.
 * 
 * <p>
 * Calling {@link #getFacadeForEntity(Entity)} returns a proxy that implements the interface
 * specified in the above {@link EntityType} to {@link Class} mapping.
 * 
 * @author Bernie Sumption
 */
public class DefaultEDRFacadeFactory implements EDRFacadeFactory {

	private final Map<Class<?>, Map<Method, Property>> methodMaps = new HashMap<Class<?>, Map<Method, Property>>();
	private final Map<EntityType, Class<?>> classMap;

	/**
	 * Create a DefaultEDRFacadeFactory using a custom mapping of {@link EntityType} to interface.
	 * 
	 * @param classMap a mapping of {@link EntityType} to {@link Class}.
	 */
	public DefaultEDRFacadeFactory(ClientEngine clientEngine, Map<EntityType, Class<?>> classMap) {
		this.classMap = classMap;
		for (EntityType entityType : classMap.keySet()) {
			Class<?> klass = classMap.get(entityType);
			methodMaps.put(klass, makeMethodMap(entityType, klass));
		}
	}

	/**
	 * Create a {@link DefaultEDRFacadeFactory}, mapping {@link EntityType}s onto interfaces by
	 * means of {@code Class.forName(entityType.getName())}
	 */
	public DefaultEDRFacadeFactory(ClientEngine clientEngine) throws ClientFacadeException {
		this(clientEngine, makeClassMap(clientEngine));
	}

	private static Map<EntityType, Class<?>> makeClassMap(ClientEngine clientEngine) throws ClientFacadeException {
		HashMap<EntityType, Class<?>> methodMap = new HashMap<EntityType, Class<?>>();
		TypeDomain typeDomain = clientEngine.getTypeDomain();
		for (EntityType entityType : UnsafeAccess.getEntityTypes(typeDomain)) {
			try {
				methodMap.put(entityType, Class.forName(entityType.getName()));
			} catch (ClassNotFoundException e) {
				throw new ClientFacadeException("There is no interface '" + e
						+ "' to map onto the entity type with the same name.");
			}
		}
		return methodMap;
	}

	/**
	 * Build a map of {@link Method} to {@link Property} used by the proxies created by
	 * {@link #getFacadeForEntity(Class, Entity)} to handle method invocations.
	 * 
	 * <p>
	 * This implementation performs the reverse of the mapping done by {@link DefaultEDRMapper} on
	 * the OGRE server, i.e. the getter method "getFooBar()" will map onto a property "foo_bar".
	 * 
	 * <p>
	 * Override this method to provide an alternative mapping strategy
	 */
	protected Map<Method, Property> makeMethodMap(EntityType entityType, Class<?> klass) {
		Map<Method, Property> methodMap = new HashMap<Method, Property>();
		for (Method method : klass.getMethods()) {
			if (Utils.isGetterMethod(method)) {
				String propertyName = Utils.getPropertyNameForGetter(method);
				Property property = entityType.getPropertyByName(propertyName);
				if (property == null) {
					throw new ClientFacadeException(entityType + " does not contain a property called '" + propertyName
							+ "'");
				}
				methodMap.put(method, property);
			}
		}
		return methodMap;
	}

	//
	// PUBLIC API
	//

	/**
	 * Locate a proxy interface for an {@link EntityType}.
	 * 
	 * <p>
	 * This method expects the value of EntityType.name to be a fully qualified class name that
	 * refers to an interface, and fetched that interface through
	 * {@code Class.forName(entityType.getName())}
	 * 
	 * <p>
	 * Override this method to change this behaviour.
	 */
	@Override
	public Class<?> getClassForEntityType(EntityType entityType) {
		return classMap.get(entityType);
	}

	/**
	 * This method creates a {@link Proxy} backed by an {@link Entity}.
	 * 
	 * <p>
	 * The proxy will implement both the Entity's interface as specified by
	 * {@link #getClassForEntityType(EntityType)}, and the {@link EntityProxy} interface.
	 * 
	 * <p>
	 * Invocations of getter methods on the proxy will be forwarded to the entity's
	 * {@link Entity#getPropertyValue(Property)} method.
	 */
	@Override
	public Object getFacadeForEntity(Entity entity) {
		Class<?> klass = classMap.get(entity.getEntityType());
		Map<Method, Property> methodMap = methodMaps.get(klass);
		return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { klass, EntityProxy.class },
				new EntityFacadeInvocationHandler(methodMap, entity));
	}
	
	private static Method TO_STRING_METHOD;
	private static Method EQUALS_METHOD;
	private static Method HASH_CODE_METHOD;
	private static Method GET_PROXIED_ENTITY_METHOD;
	
	static {
		try {
			TO_STRING_METHOD = Object.class.getMethod("toString");
			EQUALS_METHOD = Object.class.getMethod("equals", new Class<?>[] { Object.class });
			HASH_CODE_METHOD = Object.class.getMethod("hashCode");
			GET_PROXIED_ENTITY_METHOD = EntityProxy.class.getMethod("getProxiedEntity");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Forwards proxy invocations to an {@link Entity} according to the rules specified in
	 * {@link DefaultEDRFacadeFactory#getFacadeForEntity(Entity)}
	 * 
	 * @author Bernie Sumption
	 */
	private class EntityFacadeInvocationHandler implements InvocationHandler {

		private final Entity entity;
		private final Map<Method, Property> methodMap;

		public EntityFacadeInvocationHandler(Map<Method, Property> methodMap, Entity entity) {
			this.methodMap = methodMap;
			this.entity = entity;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Property property = methodMap.get(method);
			if (property != null) {
				Object propertyValue = entity.getPropertyValue(property);
				if (propertyValue instanceof Entity) {
					propertyValue = getFacadeForEntity((Entity) propertyValue);
				}
				return propertyValue;
			}
			if (method.equals(TO_STRING_METHOD)) {
				return entity.toString();
			}
			if (method.equals(EQUALS_METHOD)) {
				return args[0] == proxy;
			}
			if (method.equals(HASH_CODE_METHOD)) {
				return hashCode();
			}
			if (method.equals(GET_PROXIED_ENTITY_METHOD)) {
				return entity;
			}
			throw new ClientFacadeException("Invocation of non getter method " + method);
		}

	}

}
