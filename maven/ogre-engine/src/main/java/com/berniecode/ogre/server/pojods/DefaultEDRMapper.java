package com.berniecode.ogre.server.pojods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.ImmutableEntity;
import com.berniecode.ogre.enginelib.shared.ImmutableEntityType;
import com.berniecode.ogre.enginelib.shared.ImmutableProperty;
import com.berniecode.ogre.enginelib.shared.ImmutableTypeDomain;
import com.berniecode.ogre.enginelib.shared.IntegerPropertyType;
import com.berniecode.ogre.enginelib.shared.Property;
import com.berniecode.ogre.enginelib.shared.PropertyType;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * A {@link EDRMapper} that automatically converts from classes and objects to OGRE's Entity
 * Data Representation.
 * 
 * <p>
 * Extending this class is a good starting point for making new {@link EDRMapper}s
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
public class DefaultEDRMapper extends InitialisingBean implements EDRMapper {
	
	private Set<Class<?>> classes;
	private Map<Class<?>, EntityType> classToEntityType = new HashMap<Class<?>, EntityType>();

	private String typeDomainId;
	private TypeDomain typeDomain;
	
	//
	// INITIALISATION
	//
	
	/**
	 * Provide an ID for the mapped {@link TypeDomain}. Must be called before {@link #initialise()}
	 */
	public void setTypeDomainId(String typeDomainId) {
		requireInitialised(false, "setTypeDomainId()");
		this.typeDomainId = typeDomainId;
	}

	/**
	 * Provide a set of classes to map as a {@link TypeDomain}. Must be called before {@link #initialise()}
	 */
	public void setClasses(Set<Class<?>> classes) {
		requireInitialised(false, "setTypeDomain()");
		this.classes = classes;
	}

	/**
	 * Initialise this mapper. Before calling this method, a {@link #setTypeDomainId(String)} and
	 * {@link #setClasses(Set)} must have been called.
	 */
	protected final void doInitialise() {
		requireNotNull(typeDomainId, "typeDomainId");
		requireNotNull(classes, "classes");

		for (Class<?> klass : classes) {
			classToEntityType.put(klass, createEntityType(klass));
		}
		
		List<EntityType> entityTypes = new ArrayList<EntityType>(classToEntityType.values());
		
		Collections.sort(entityTypes, NamedComparator.INSTANCE);

		typeDomain = new ImmutableTypeDomain(typeDomainId, entityTypes.toArray(new EntityType[0]));
	}

	//
	// TYPE DOMAIN MAPPING
	//

	/**
	 * Get the {@link TypeDomain} associated mapped by this {@link DefaultEDRMapper}
	 */
	public TypeDomain getTypeDomain() {
		return typeDomain;
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
		
		return new ImmutableEntityType(name, properties.toArray(new Property[0]));
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
	// OBJECT GRAPH MAPPING
	//

	/**
	 * Convert an {@link Object} into an {@link Entity} with the specified id.
	 */
	@Override
	public final Entity createEntity(Object object, long id) {
		requireInitialised(true, "createEntity()");
		EntityType entityType = classToEntityType.get(object.getClass());
		if (entityType == null) {
			throw new OgreException("Can't create an Entity for object of type " + object.getClass()
					+ " because this PojoDataSource was not initialised with that class.'");
		}
		return doCreateEntity(object, id, entityType);
	}
	
	private Entity doCreateEntity(Object object, long id, EntityType entityType) {
		return new ImmutableEntity(entityType, id, new Object[0]); // FIXME do entity value mapping here
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
