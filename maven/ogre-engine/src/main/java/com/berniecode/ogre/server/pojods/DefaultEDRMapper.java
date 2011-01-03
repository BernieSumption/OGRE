package com.berniecode.ogre.server.pojods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.IntegerProperty;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.ReferenceProperty;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.UnsafeAccess;

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

	
	private Set<Class<?>> classes;
	private IdMapper idMapper;
	private String typeDomainId;
	private TypeDomain typeDomain;

	Map<Class<?>, EntityType> classToEntityType = new HashMap<Class<?>, EntityType>();
	private Map<Property, Method> propertyToMethod = new HashMap<Property, Method>();

	
	//
	// INITIALISATION
	//
	
	/**
	 * Construct a uninitialised {@link DefaultEDRMapper}.
	 */
	public DefaultEDRMapper() {}
	
	/**
	 * Construct and initialise a {@link DefaultEDRMapper} with the specified type domain id and set
	 * of classes
	 */
	public DefaultEDRMapper(String typeDomainId, Class<?> ... classes) {
		setTypeDomainId(typeDomainId);
		setClasses(classes);
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
	public void setClasses(Class<?>... classes) {
		requireInitialised(false, "setTypeDomain()");
		Arrays.sort(classes, new ClassNameComparator());
		this.classes = new LinkedHashSet<Class<?>>(Arrays.asList(classes)); 
	}

	/**
	 * Provide an alternative {@link IdMapper} implementation. If called at all, this method must be
	 * called before {@link #initialise()}. If it is not called before {@link #initialise()},
	 * {@link DefaultIdMapper} will be used.
	 */
	public void setIdMapper(IdMapper idMapper) {
		requireInitialised(false, "setIdMapper()");
		this.idMapper = idMapper;
	}

	/**
	 * Initialise this mapper. Before calling this method, a {@link #setTypeDomainId(String)} and
	 * {@link #setClasses(Set)} must have been called.
	 */
	@Override
	protected final void doInitialise() {
		requireNotNull(typeDomainId, "typeDomainId");
		requireNotNull(classes, "classes");
		
		List<EntityType> entityTypes = new ArrayList<EntityType>();
		int index = 0;
		List<Class<?>> processedClasses = new ArrayList<Class<?>>();
		for (Class<?> klass : classes) {
			for (Class<?> otherClass: processedClasses) {
				if (otherClass.isAssignableFrom(klass) || klass.isAssignableFrom(otherClass)) {
					throw new TypeMappingException("The class '" + klass + "' can't be mapped because it is a supertype or subtype of '" + otherClass + "'");
				}
			}
			EntityType entityType = createEntityType(index++, klass);
			classToEntityType.put(klass, entityType);
			entityTypes.add(entityType);
			processedClasses.add(klass);
		}
		
		typeDomain = new TypeDomain(typeDomainId, entityTypes.toArray(new EntityType[0]));
		
		
		
		if (idMapper == null) {
			idMapper = new DefaultIdMapper();
			for (Class<?> klass : classes) {
				try {
					if (klass.getMethod("equals", Object.class).getDeclaringClass() != Object.class) {
						OgreLog.warn(
								"The class " + klass + " overrides the Object.equals method, but no custom IdMapper " +
								"has been provided. By default, DefaultEDRMapper uses a HashMap to track the mappoing of " +
								"objects to their ID. If the equals() identity of an object changes at any point during " +
								"its lifetime, it will be assinged a new ID and all references to it will break. It is suggested " +
								"that you provide a custom IdMapper implementation, unless you are really sure that your objects " +
								"equality never changes");
					}
				} catch (Exception e) {
					// never happens, I promise
				}
			}
		}
	}

	//
	// TYPE DOMAIN MAPPING
	//

	/**
	 * Get the {@link TypeDomain} mapped by this {@link DefaultEDRMapper}
	 */
	@Override
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
				Property property = createProperty(method, propertyIndex++);
				if (property != null) {
					properties.add(property);
				} else {
					OgreLog.warn("Ignoring non-mappable getter method " + method);
				}
			}
		}
		
		return new EntityType(entityTypeIndex, name, properties.toArray(new Property[0]));
	}

	/**
	 * Choose an OGRE property name for a given getter method. The default implementation is
	 * {@link Utils#getPropertyNameForGetter(Method)} which converts getJavaBeanProperty into
	 * "java_bean_property"
	 */
	protected String getPropertyNameForMethod(Method method) {
		return Utils.getPropertyNameForGetter(method);
	}

	/**
	 * Convert a {@link Method} to a {@link Property}. This method can be overridden by subclasses that
	 * want to alter the default mapping behaviour.
	 * 
	 * <p>If this method returns null, the property will be ignored
	 */
	protected Property createProperty(Method method, int propertyIndex) {
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		String name = getPropertyNameForMethod(method);
		Class<?> type = method.getReturnType();
		boolean nullable = !type.isPrimitive();
		Property property;
		
		if (integerTypes.containsKey(type)) {
			property = new IntegerProperty(propertyIndex, name, integerTypes.get(type), nullable);
		}
		else if (classes.contains(type)) {
			property = new ReferenceProperty(propertyIndex, name, getEntityTypeNameForClass(type));
		}
		else if (type == float.class || type == Float.class) {
			property = new Property(propertyIndex, name, Property.TYPECODE_FLOAT, nullable);
		}
		else if (type == double.class || type == Double.class) {
			property = new Property(propertyIndex, name, Property.TYPECODE_DOUBLE, nullable);
		}
		else if (type == String.class) {
			property = new Property(propertyIndex, name, Property.TYPECODE_STRING, true);
		}
		else if (type == byte[].class) {
			property = new Property(propertyIndex, name, Property.TYPECODE_BYTES, true);
		} else {
			return null;
		}
		
		propertyToMethod.put(property, method);
		return property;
	}

	// map of Java's integer types to their bitlength
	private final Map<Class<?>, Integer> integerTypes;
	{
		integerTypes = new HashMap<Class<?>, Integer>();
		integerTypes.put(long.class, 64);
		integerTypes.put(Long.class, 64);
		integerTypes.put(int.class, 32);
		integerTypes.put(Integer.class, 32);
		integerTypes.put(short.class, 16);
		integerTypes.put(Short.class, 16);
		integerTypes.put(byte.class, 8);
		integerTypes.put(Byte.class, 8);
	}
	
	//
	// OBJECT GRAPH MAPPING
	//

	/**
	 * @see com.berniecode.ogre.server.pojods.IdMapper#getIdForObject(Object)
	 */
	@Override
	public long getIdForObject(Object entityObject) {
		requireInitialised(true, "getIdForObject()");
		return idMapper.getIdForObject(entityObject);
	}

	/**
	 * @see com.berniecode.ogre.server.pojods.IdMapper#objectHasId(Object)
	 */
	@Override
	public boolean objectHasId(Object entityObject) {
		requireInitialised(true, "objectHasId()");
		return idMapper.objectHasId(entityObject);
	}

	/**
	 * Convert an {@link Object} into an {@link Entity} with the specified id.
	 */
	@Override
	public final Entity createEntity(Object entityObject) {
		requireInitialised(true, "createEntity()");
		
		EntityType entityType = getEntityTypeForObject(entityObject);
		
		Object[] values = new Object[entityType.getPropertyCount()];
		
		for (int i=0; i<values.length; i++) {
			values[i] = getValueForProperty(entityObject, entityType.getProperty(i));
		}
		
		return new Entity(entityType, getIdForObject(entityObject), values);
	}
	

	Map<Class<?>, EntityType> entityTypeForObjectCache = new HashMap<Class<?>, EntityType>();

	/**
	 * Get the {@link EntityType} for an object
	 */
	@Override
	public EntityType getEntityTypeForObject(Object entityObject) {
		requireInitialised(true, "getEntityTypeForObject()");
		
		Class<? extends Object> entityClass = entityObject.getClass();
		EntityType entityType = entityTypeForObjectCache.get(entityClass);
		if (entityType != null) {
			return entityType;
		}
		
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
		entityTypeForObjectCache.put(entityClass, entityType);
		return entityType;
	}

	@Override
	public List<Object> getRelatedObjects(Object entityObject) {
		requireInitialised(true, "getRelatedObjects()");
		
		EntityType entityType = getEntityTypeForObject(entityObject);
		

		List<Object> relatedObjects = new ArrayList<Object>();
		
		ReferenceProperty[] referenceProperties = entityType.getReferenceProperties();
		for (int i = 0; i < referenceProperties.length; i++) {
			relatedObjects.add(getRawValueForProperty(entityObject, referenceProperties[i]));
		}

		return relatedObjects;
	}

	protected Object getValueForProperty(Object object, Property property) {
		Object o = getRawValueForProperty(object, property);
		if (property instanceof ReferenceProperty) {
			return getIdForObject(o);
		}
		return o;
	}

	protected Object getRawValueForProperty(Object object, Property property) {
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
	
	
	/**
	 * The default {@link IdMapper}, automatically assigns an ID to each object.
	 *
	 * @author Bernie Sumption
	 */
	class DefaultIdMapper implements IdMapper {
		
		// stores a map of object to id, for each EntityType
		Map<EntityType, Map<Object, Long>> idMap = new HashMap<EntityType, Map<Object,Long>>();
		
		// stores the current assigned ID, for each EntityType
		Map<EntityType, Long> nextFreeId = new HashMap<EntityType, Long>();
		
		public DefaultIdMapper() {
			for (EntityType entityType: UnsafeAccess.getEntityTypes(typeDomain)) {
				idMap.put(entityType, new WeakHashMap<Object, Long>());
				nextFreeId.put(entityType, 1L);
			}
		}

		@Override
		public synchronized long getIdForObject(Object entityObject) {

			EntityType entityType = getEntityTypeForObject(entityObject);
			
			Long existingId = idMap.get(entityType).get(entityObject);
			if (existingId != null) {
				return existingId;
			}
			long thisId = nextFreeId.get(entityType);
			nextFreeId.put(entityType, thisId + 1);
			if (OgreLog.isDebugEnabled()) {
				OgreLog.debug("Assigned id " +  thisId + " to object '" + entityObject + "'");
			}
			idMap.get(entityType).put(entityObject, thisId);
			return thisId;
		}


		@Override
		public synchronized boolean objectHasId(Object entityObject) {
			EntityType entityType = getEntityTypeForObject(entityObject);
			return idMap.get(entityType).containsKey(entityObject);
		}
	}
}
