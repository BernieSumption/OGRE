package com.berniecode.ogre.server.pojods;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.server.DataSource;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.ObjectGraph;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * A {@link DataSource} that extracts a {@link TypeDomain} from a set of java classes and an
 * {@link ObjectGraph} from from a set of java objects
 * 
 * @author Bernie Sumption
 */
public class PojoDataSource extends InitialisingBean implements DataSource {

	private Set<Class<?>> classes;

	TypeDomainMapper typeDomainMapper;
	private String typeDomainId;
	TypeDomain typeDomain;

	private String objectGraphId;

	// the entities in this object graph, stored as a map of entity name to map of entity id to
	// Entity (Ah, maps of maps, you can tell that I learned PHP before Java ;o)
	private Map<EntityType, Map<Integer, Entity>> entities;
	
	//
	// INITIALISATION
	//

	// Check that all required fields are present
	@Override
	protected void doInitialise() {
		requireNotNull(classes, "classes");
		requireNotNull(typeDomainMapper, "typeDomainMapper");
		requireNotNull(typeDomainId, "typeDomainId");
		requireNotNull(entityMapper, "entityMapper");
		requireNotNull(objectGraphId, "objectGraphId");
		
		if (typeDomainMapper == null) {
			DefaultMapper defaultMapper = new DefaultMapper();
			defaultMapper.initialise(typeDomainId, classes);
		}
		
		typeDomain = typeDomainMapper.initialise(typeDomainId, classes);
		entities = new HashMap<EntityType, Map<Integer, Entity>>();
		for (EntityType entityType: typeDomain.getEntityTypes()) {
			entities.put(entityType, new HashMap<Integer, Entity>());
		}
	}

	/**
	 * Set the classes used to create the type domain. Must be called before initialise();
	 */
	public void setClasses(Class<?>... classes) {
		requireInitialised(false, "setClasses()");
		this.classes = Utils.arrayToSet(classes);
	}

	/**
	 * Provide an alternative {@link TypeDomainMapper}.
	 * 
	 * If used at all, this method must be called before initialise(). If no alternative
	 * {@link TypeDomainMapper} is provided, {@link DefaultMapper} will be used
	 */
	public void setTypeDomainMapper(TypeDomainMapper typeDomainMapper) {
		requireInitialised(false, "setTypeDomainMapper()");
		this.typeDomainMapper = typeDomainMapper;
	}

	/**
	 * Must be called before initialise();
	 */
	public void setTypeDomainId(String typeDomainId) {
		requireInitialised(false, "setTypeDomainId()");
		this.typeDomainId = typeDomainId;
	}

	/**
	 * Must be called before initialise();
	 */
	public void setObjectGraphId(String objectGraphId) {
		requireInitialised(false, "setObjectGraphId()");
		this.objectGraphId = objectGraphId;
	}
	
	//
	// DataSource INTERFACE IMPLEMENTATION
	//

	@Override
	public TypeDomain getTypeDomain() {
		requireInitialised(true, "getTypeDomain()");
		return typeDomain;
	}

	@Override
	public String getObjectGraphId() {
		return objectGraphId;
	}
	
	//
	// PUBLIC API
	//

	/**
	 * Add objects to the object graph.
	 * 
	 * <p>
	 * Each object must be an instance of one of the classes passed to {@link #setClasses(Class...)}
	 * 
	 * TODO test the above limitation
	 * 
	 * TODO when entity updating is implemented, test that only one update message is produced
	 * 
	 * <p>
	 * {@link #initialise()} must be called before this method can be used()
	 */
	public void addEntityObjects(Object ... entityObjects) {
		requireInitialised(true, "setEntityObjects()");
		for (Object entityObject: entityObjects) {
			//FIXME create and use IdMapper here
			Entity entity = entityMapper.createEntity(entityObject, 0, typeDomain);
		}
	}

}
