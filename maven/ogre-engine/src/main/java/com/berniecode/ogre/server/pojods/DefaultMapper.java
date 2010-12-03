package com.berniecode.ogre.server.pojods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.Entity;
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
 * Data Representation.
 * 
 * <p>
 * Extending this class is a good starting point for making new {@link TypeDomainMapper}s
 * 
 * <ul>
 * <li>entity names are fully qualified class names
 * <li>property names are derived by converting JavaBean style property getters like
 * getSomeProperty() into the recommended OGRE style "some_property"
 * <li>property types are derived from JavaBean getter return types. Mappings are defined for
 * Strings, java primitives and their boxed counterparts
 * </ul>
 * 
 * TODO test all above assertions
 * TODO test ALL primitives are supported
 * 
 * @author Bernie Sumption
 */
public class DefaultMapper implements TypeDomainMapper {
	
	//
	// TypeDomainMapper IMPLEMENTATION
	//

	/**
	 * Create a {@link TypeDomain} from a set of {@link Class}es.
	 * 
	 * <p>
	 * When overriding this method to alter the mapping behaviour, make sure you also override the
	 * equivalent {@link EntityMapper} method {@link #createEntity(Object, long, TypeDomain)} which depends on the
	 * behaviour of this method.
	 * 
	 * TODO add this comment to lower methods once mapEntity is completed
	 */
	@Override
	public TypeDomain initialise(String typeDomainId, Set<Class<?>> classes) {
		
		List<EntityType> entityTypes = new ArrayList<EntityType>();

		for (Class<?> klass : classes) {
			entityTypes.add(createEntityType(klass));
		}
		
		Collections.sort(entityTypes, NamedComparator.INSTANCE);

		TypeDomain td = new ImmutableTypeDomain(typeDomainId, entityTypes.toArray(new EntityType[0]));
		return td;
	}

	/**
	 * Convert a {@link Class} to an {@link EntityType}.
	 */
	protected EntityType createEntityType(Class<?> klass) {
		if (klass.isAnnotation() || klass.isInterface() || klass.isArray() || klass.isAnonymousClass()
				|| klass.isEnum() || klass.isPrimitive()) {
			throw new OgreException("The class '" + klass + "' can't be mapped because it is not a regular concrete class");
		}
		String name = getEntityTypeNameForClass(klass);
		List<Property> properties = new ArrayList<Property>();
		for (Method method : klass.getMethods()) {
			if (Utils.isGetterMethod(method)) {
				properties.add(createProperty(method));
			}
		}
		
		Collections.sort(properties, NamedComparator.INSTANCE);
		
		return new ImmutableEntityType(name, Utils.listToOrderedCollection(properties));
	}

	/**
	 * Convert a {@link Method} to a {@link Property}. This method can be overridden by subclasses that
	 * want to alter the default mapping behaviour.
	 */
	protected Property createProperty(Method method) {
		PropertyType propertyType = createPropertyType(method.getReturnType());
		return new ImmutableProperty(Utils.getPropertyNameForGetter(method), propertyType);
	}

	/**
	 * Convert the return type of a {@link Method} to a {@link PropertyType} for the associated
	 * {@link Property}. This method can be overridden by subclasses that want to alter the default
	 * mapping behaviour.
	 */
	protected PropertyType createPropertyType(Class<?> javaType) {
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
	
	//
	// TypeEntityMapper IMPLEMENTATION
	//

	/**
	 * Convert an {@link Object} into an {@link Entity} with the specified id.
	 */
	@Override
	public Entity createEntity(Object object, long id, TypeDomain typeDomain) {
		String entityTypeName = getEntityTypeNameForClass(object.getClass());
		try {
			EntityType entityType = typeDomain.getEntityTypeByName(entityTypeName);
		} catch (NoSuchThingException e) {
			throw new OgreException("Can't create an Entity for object of type " + object.getClass()
					+ " because the TypeDomain '" + typeDomain.getTypeDomainId()
					+ "' does not contain the entity type '" + entityTypeName + "'");
		}
		return null;
	}
	
	//
	// SHARED METHODS
	//

	/**
	 * Controls the Class to EntityType.name mapping used by
	 * {@link #createEntity(Object, long, TypeDomain)} and {@link #createEntityType(Class)}
	 * 
	 * <p>By default, the fully qualified class name is used
	 */
	protected String getEntityTypeNameForClass(Class<?> klass) {
		return klass.getName();
	}

}
