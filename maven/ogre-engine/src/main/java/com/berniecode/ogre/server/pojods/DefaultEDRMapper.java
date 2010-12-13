package com.berniecode.ogre.server.pojods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.FloatPropertyType;
import com.berniecode.ogre.enginelib.shared.IntegerPropertyType;
import com.berniecode.ogre.enginelib.shared.Property;
import com.berniecode.ogre.enginelib.shared.PropertyType;
import com.berniecode.ogre.enginelib.shared.StringPropertyType;
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
 * @author Bernie Sumption
 */
public class DefaultEDRMapper extends InitialisingBean implements EDRMapper {
	
	private Class<?>[] classes;
	Map<Class<?>, EntityType> classToEntityType = new HashMap<Class<?>, EntityType>();
	private Map<Property, Method> propertyToMethod = new HashMap<Property, Method>();

	private String typeDomainId;
	private TypeDomain typeDomain;
	
	//
	// INITIALISATION
	//
	
	public DefaultEDRMapper() {}
	
	/**
	 * Construct and initialise a {@link DefaultEDRMapper} with the specified type domain id and set
	 * of classes
	 */
	public DefaultEDRMapper(String typeDomainId, Class<?> ... classes) {
		this.typeDomainId = typeDomainId;
		this.classes = classes;
		initialise();
	}

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
	public void setClasses(Class<?>[] classes) {
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

		Arrays.sort(classes, new ClassNameComparator());

		List<EntityType> entityTypes = new ArrayList<EntityType>();
		
		int index = 0;
		for (Class<?> klass : classes) {
			for (Class<?> otherClass: classToEntityType.keySet()) {
				if (otherClass.isAssignableFrom(klass) || klass.isAssignableFrom(otherClass)) {
					throw new TypeMappingException("The class '" + klass + "' can't be mapped because it is a supertype or subtype of '" + otherClass + "'");
				}
			}
			EntityType entityType = createEntityType(index++, klass);
			classToEntityType.put(klass, entityType);
			entityTypes.add(entityType);
		}
		

		typeDomain = new TypeDomain(typeDomainId, entityTypes.toArray(new EntityType[0]));
	}

	//
	// TYPE DOMAIN MAPPING
	//

	/**
	 * Get the {@link TypeDomain} associated mapped by this {@link DefaultEDRMapper}
	 */
	public TypeDomain getTypeDomain() {
		requireInitialised(true, "getTypeDomain()");
		return typeDomain;
	}

	/**
	 * Convert a {@link Class} to an {@link EntityType}.
	 * @param entityTypeIndex 
	 */
	protected EntityType createEntityType(int entityTypeIndex, Class<?> klass) {
		if (klass.isAnonymousClass()) {
			throw new TypeMappingException("The class '" + klass + "' can't be mapped because it is an anonymous type");
		}
		if (klass.isArray()) {
			throw new TypeMappingException("The class '" + klass + "' can't be mapped because it is an array type");
		}
		if (klass.isEnum()) {
			throw new TypeMappingException("The class '" + klass + "' can't be mapped because it is an enum type");
		}
		if (klass.isPrimitive()) {
			throw new TypeMappingException("The class '" + klass + "' can't be mapped because it is a primitive type");
		}
		String name = getEntityTypeNameForClass(klass);
		List<Property> properties = new ArrayList<Property>();
		Method[] methods = klass.getMethods();
		Arrays.sort(methods, new MethodNameComparator());
		int propertyIndex = 0;
		for (Method method : methods) {
			if (Utils.isGetterMethod(method)) {
				properties.add(createProperty(method, propertyIndex++));
			}
		}
		
		return new EntityType(entityTypeIndex, name, properties.toArray(new Property[0]));
	}

	/**
	 * Convert a {@link Method} to a {@link Property}. This method can be overridden by subclasses that
	 * want to alter the default mapping behaviour.
	 */
	protected Property createProperty(Method method, int propertyIndex) {
		PropertyType propertyType = createPropertyType(method.getReturnType());
		Property property = new Property(propertyIndex, Utils.getPropertyNameForGetter(method), propertyType);
		propertyToMethod.put(property, method);
		return property;
	}
	
	private final Map<Class<?>, PropertyType> classToPropertyType;
	
	{
		classToPropertyType = new HashMap<Class<?>, PropertyType>();
		registerClassMapping(long.class,    new IntegerPropertyType(64, false));
		registerClassMapping(Long.class,    new IntegerPropertyType(64, true));
		registerClassMapping(int.class,     new IntegerPropertyType(32, false));
		registerClassMapping(Integer.class, new IntegerPropertyType(32, true));
		registerClassMapping(short.class,   new IntegerPropertyType(16, false));
		registerClassMapping(Short.class,   new IntegerPropertyType(16, true));
		registerClassMapping(byte.class,    new IntegerPropertyType(8, false));
		registerClassMapping(Byte.class,    new IntegerPropertyType(8, true));
		registerClassMapping(String.class,  new StringPropertyType());
		registerClassMapping(float.class,   new FloatPropertyType(32, false));
		registerClassMapping(Float.class,   new FloatPropertyType(32, true));
		registerClassMapping(double.class,  new FloatPropertyType(64, false));
		registerClassMapping(Double.class,  new FloatPropertyType(64, true));
	}

	/**
	 * Register a mapping from a Java method return type to an OGRE {@link PropertyType}
	 */
	public void registerClassMapping(Class<?> klass, PropertyType propertyType) {
		classToPropertyType.put(klass, propertyType);
	}

	/**
	 * Convert the return type of a {@link Method} to a {@link PropertyType} for the associated
	 * {@link Property}. This method can be overridden by subclasses that want to alter the default
	 * mapping behaviour.
	 */
	protected PropertyType createPropertyType(Class<?> javaType) throws TypeMappingException {
		PropertyType propertyType = classToPropertyType.get(javaType);
		if (propertyType != null) {
			return propertyType;
		}
		throw new TypeMappingException("No PropertyType mapping defined for class " + javaType);
	}
	
	//
	// OBJECT GRAPH MAPPING
	//

	/**
	 * Convert an {@link Object} into an {@link Entity} with the specified id.
	 */
	@Override
	public final Entity createEntity(Object entityObject, long id) {
		requireInitialised(true, "createEntity()");
		
		EntityType entityType = getEntityTypeForObject(entityObject);
		
		List<Object> values = new ArrayList<Object>();
		
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			values.add(getValueForProperty(entityObject, entityType.getProperty(i)));
		}
		
		return new Entity(entityType, id, values.toArray());
	}

	/**
	 * Get the {@link EntityType} for an object
	 */
	public EntityType getEntityTypeForObject(Object entityObject) {
		Class<? extends Object> entityClass = entityObject.getClass();
		EntityType entityType = null;
		for (Class<?> klass: classToEntityType.keySet()) {
			if (klass.isAssignableFrom(entityClass)) {
				if (entityType == null) {
					entityType = classToEntityType.get(klass);
				} else {
					throw new ValueMappingException("Can't choose an EntityType for object of type " + entityObject.getClass()
							+ " because it matches two EntityTypes: '" + entityType + "' and '" + classToEntityType.get(klass) + "'");
				}
			}
		}
		if (entityType == null) {
			throw new ValueMappingException("Can't choose EntityType for object of type " + entityObject.getClass()
					+ " because PojoDataSource was not initialised with that class or a supertype of that class.");
		}
		return entityType;
	}

	protected Object getValueForProperty(Object object, Property property) {
		Method getter = propertyToMethod.get(property);
		try {
			return getter.invoke(object);
		} catch (Throwable t) {
			throw new ValueMappingException("Exception thrown while invoking getter method " + getter, t);
		}
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
