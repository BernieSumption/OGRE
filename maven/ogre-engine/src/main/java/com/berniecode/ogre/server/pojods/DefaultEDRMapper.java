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
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.shared.BytesPropertyType;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.FloatPropertyType;
import com.berniecode.ogre.enginelib.shared.IntegerPropertyType;
import com.berniecode.ogre.enginelib.shared.Property;
import com.berniecode.ogre.enginelib.shared.PropertyType;
import com.berniecode.ogre.enginelib.shared.ReferencePropertyType;
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

	
	private Set<Class<?>> classes;
	private IdMapper idMapper;
	private String typeDomainId;
	private TypeDomain typeDomain;

	Map<Class<?>, EntityType> classToEntityType = new HashMap<Class<?>, EntityType>();
	private Map<Property, Method> propertyToMethod = new HashMap<Property, Method>();

	
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

		for (Class<?> klass : classes) {
			registerClassMapping(klass, new ReferencePropertyType(getEntityTypeNameForClass(klass)));
			
			try {
				if (klass.getMethod("equals", Object.class).getDeclaringClass() != Object.class
						&& idMapper instanceof DefaultIdMapper) {
					OgreLog.warn(
							"The class " + klass + " overrides the Object.equals method. DefaultIdMapper " +
							"uses a HashMap to assign IDs to objects. If the equals() identity of an object" +
							"changes at any point during its lifetime, this method will break. It is suggested " +
							"that you provide a custom IdMapper implementation if your objects equality ever changes");
				}
			} catch (Exception e) {
				// never happens
			}
		}
		
		
		
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
				registerClassMapping(klass, new ReferencePropertyType(getEntityTypeNameForClass(klass)));
				
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
					// never happens
				}
			}
		}
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
			if (Utils.isGetterMethod(method) && isMappableMethod(method)) {
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				properties.add(createProperty(method, propertyIndex++));
			}
		}
		
		return new EntityType(entityTypeIndex, name, properties.toArray(new Property[0]));
	}

	/**
	 * Override this method to control whether individual getter methods are mapped to OGRE properties. If
	 * this method returns true for a property, it will be mapped to a property.
	 * 
	 * <p>The default behaviour is to map any getter methods that have mappable return types.
	 */
	protected boolean isMappableMethod(Method method) {
		return classToPropertyType.containsKey(method.getReturnType());
	}

	/**
	 * Choose an OGRE property name for a given getter method. The default implementation is
	 * {@link Utils#getPropertyNameForGetter(Method)} which converts getJavaBeanProperty into
	 * "java_bean_property"
	 */
	protected String getPropertyNameForMethod(Method method) {
		//TODO test that the class still works if this is overridden
		return Utils.getPropertyNameForGetter(method);
	}

	/**
	 * Convert a {@link Method} to a {@link Property}. This method can be overridden by subclasses that
	 * want to alter the default mapping behaviour.
	 */
	protected Property createProperty(Method method, int propertyIndex) {
		PropertyType propertyType = createPropertyType(method.getReturnType());
		Property property = new Property(propertyIndex, getPropertyNameForMethod(method), propertyType);
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
		registerClassMapping(byte[].class,  new BytesPropertyType());
	}

	/**
	 * Register a mapping from a Java method return type to an OGRE {@link PropertyType}
	 */
	protected void registerClassMapping(Class<?> klass, PropertyType propertyType) {
		classToPropertyType.put(klass, propertyType);
	}

	/**
	 * Convert the return type of a {@link Method} to a {@link PropertyType} for the associated
	 * {@link Property}. This method can be overridden by subclasses that want to alter the default
	 * mapping behaviour.
	 */
	protected PropertyType createPropertyType(Class<?> javaType) throws TypeMappingException {
		requireInitialised(true, "createPropertyType()");
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
		
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			if (property.getPropertyType() instanceof ReferencePropertyType) {
				relatedObjects.add(getRawValueForProperty(entityObject, property));
			}
		}
		return relatedObjects;
	}

	protected Object getValueForProperty(Object object, Property property) {
		Object o = getRawValueForProperty(object, property);
		if (property.getPropertyType() instanceof ReferencePropertyType) {
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
			for (EntityType entityType: typeDomain.getEntityTypes()) {
				idMap.put(entityType, new WeakHashMap<Object, Long>());
				//TODO test that unused objects are freed.
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
