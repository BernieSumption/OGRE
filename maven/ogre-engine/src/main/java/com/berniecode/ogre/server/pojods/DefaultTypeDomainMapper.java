package com.berniecode.ogre.server.pojods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.ImmutableEntityType;
import com.berniecode.ogre.enginelib.shared.ImmutableProperty;
import com.berniecode.ogre.enginelib.shared.ImmutableTypeDomain;
import com.berniecode.ogre.enginelib.shared.IntegerPropertyType;
import com.berniecode.ogre.enginelib.shared.Property;
import com.berniecode.ogre.enginelib.shared.PropertyType;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * A {@link TypeDomainMapper} that automatically converts from classes and objects to OGRE's Entity
 * Data Representation
 * 
 * @author Bernie Sumption
 */
public class DefaultTypeDomainMapper implements TypeDomainMapper {

	@Override
	public TypeDomain mapTypeDomain(String typeDomainId, Set<Class<?>> classes) {
		
		List<EntityType> entityTypes = new ArrayList<EntityType>();

		for (Class<?> klass : classes) {
			entityTypes.add(mapEntityType(klass));
		}
		
		Collections.sort(entityTypes, NamedComparator.INSTANCE);

		TypeDomain td = new ImmutableTypeDomain(typeDomainId, Utils.listToOrderedCollection(entityTypes));
		return td;
	}

	private EntityType mapEntityType(Class<?> klass) {
		if (klass.isAnnotation() || klass.isInterface() || klass.isArray() || klass.isAnonymousClass()
				|| klass.isEnum() || klass.isPrimitive()) {
			throw new OgreException("The class '" + klass + "' can't be mapped because it is not a regular concrete class");
		}
		String name = klass.getName();
		List<Property> properties = new ArrayList<Property>();
		for (Method method : klass.getMethods()) {
			if (Utils.isGetterMethod(method)) {
				properties.add(mapProperty(method));
			}
		}
		
		Collections.sort(properties, NamedComparator.INSTANCE);
		
		return new ImmutableEntityType(name, Utils.listToOrderedCollection(properties));
	}

	private Property mapProperty(Method method) {
		PropertyType propertyType = mapPropertyType(method.getReturnType());
		return new ImmutableProperty(Utils.getPropertyNameForGetter(method), propertyType);
	}

	private PropertyType mapPropertyType(Class<?> javaType) {
		if (javaType == long.class) {
			return new IntegerPropertyType(64, false);
		}
		if (javaType == Long.class) {
			return new IntegerPropertyType(64, true);
		}
		if (javaType == int.class) {
			return new IntegerPropertyType(32, false);
		}
		if (javaType == Integer.class) {
			return new IntegerPropertyType(32, true);
		}
		if (javaType == short.class) {
			return new IntegerPropertyType(16, false);
		}
		if (javaType == Short.class) {
			return new IntegerPropertyType(16, true);
		}
		if (javaType == byte.class) {
			return new IntegerPropertyType(8, false);
		}
		if (javaType == Byte.class) {
			return new IntegerPropertyType(8, true);
		}
		throw new OgreException("No PropertyType mapping defined for class " + javaType);
	}

}
