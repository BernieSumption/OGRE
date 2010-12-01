package com.berniecode.ogre.server.pojods;

import java.lang.reflect.Method;
import java.util.Set;

import com.berniecode.ogre.OgreException;
import com.berniecode.ogre.Utils;
import com.berniecode.ogre.engine.platformhooks.NativeOrderedCollection;
import com.berniecode.ogre.engine.shared.EntityType;
import com.berniecode.ogre.engine.shared.OrderedCollection;
import com.berniecode.ogre.engine.shared.Property;
import com.berniecode.ogre.engine.shared.PropertyType;
import com.berniecode.ogre.engine.shared.TypeDomain;
import com.berniecode.ogre.engine.shared.impl.ImmutableEntityType;
import com.berniecode.ogre.engine.shared.impl.ImmutableProperty;
import com.berniecode.ogre.engine.shared.impl.ImmutableTypeDomain;

public class DefaultTypeDomainMapper implements TypeDomainMapper {

	@Override
	public TypeDomain mapTypeDomain(String typeDomainId, Set<Class<?>> classes) {
		OrderedCollection entityTypes = new NativeOrderedCollection();

		for (Class<?> klass : classes) {
			entityTypes.push(mapEntityType(klass));
		}

		TypeDomain td = new ImmutableTypeDomain(typeDomainId, entityTypes);
		return td;
	}

	private EntityType mapEntityType(Class<?> klass) {
		if (klass.isAnnotation() || klass.isInterface() || klass.isArray() || klass.isAnonymousClass()
				|| klass.isEnum() || klass.isPrimitive()) {
			throw new OgreException("The class '" + klass
					+ "' can't be mapped because it is not a regular concrete class");
		}
		String name = klass.getName();
		OrderedCollection properties = new NativeOrderedCollection();
		for (Method method : klass.getMethods()) {
			if (Utils.isGetterMethod(method)) {
				properties.push(mapProperty(method));
			}
		}
		return new ImmutableEntityType(name, properties);
	}

	private Property mapProperty(Method method) {
		PropertyType propertyType = null;
		return new ImmutableProperty(Utils.getPropertyNameForGetter(method), propertyType);
	}

}
