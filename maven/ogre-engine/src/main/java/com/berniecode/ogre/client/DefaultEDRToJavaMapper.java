package com.berniecode.ogre.client;

import java.lang.reflect.Proxy;

import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;

/**
 * This implementation of {@link EDRToJavaMapper} uses {@link Proxy} instances to provide a strongly
 * typed facade for {@link Entity} objects.
 * 
 * @author Bernie Sumption
 */
public class DefaultEDRToJavaMapper implements EDRToJavaMapper {

	/**
	 * This method expects the value of EntityType.name to be a fully qualified class name that
	 * refers to an interface.
	 * 
	 * <p>
	 * This will be the case if the server is using {@link DefaultEDRMapper}, and the
	 * {@link DefaultEDRMapper} has been initialised by passing only interfaces to
	 * {@link DefaultEDRMapper#setClasses(Class...)}, not concrete classes.
	 */
	@Override
	public Class<?> getClassForEntityType(EntityType entityType) {
		try {
			Class<?> klass = Class.forName(entityType.getName());
			if (!klass.isInterface()) {
				throw new OgreException("The class " + klass.getName() + " is not an interface, so can't be mapped by DefaultEDRToJavaMapper");
			}
			return klass;
		} catch (ClassNotFoundException e) {
			throw new OgreException("Problem creating class for entity type", e);
		}
	}

	/**
	 * This method creates a {@link Proxy} backed by a 
	 */
	@Override
	public <T> T getFacadeForEntity(Class<T> klass, Entity entity) {
		return klass.cast(Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { klass }, EntityFacadeInvocationHandler.newInstance(klass, entity)));
	}

}
