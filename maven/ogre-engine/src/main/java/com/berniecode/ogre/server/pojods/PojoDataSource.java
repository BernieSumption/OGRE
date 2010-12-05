package com.berniecode.ogre.server.pojods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.Utils;
import com.berniecode.ogre.enginelib.server.DataSource;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.ImmutableObjectGraph;
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

	EDRMapper edrMapper;
	private String typeDomainId;
	TypeDomain typeDomain;

	private String objectGraphId;

	// the entities in this object graph, stored as a map of entity name to map of entity id to
	// Entity (Ah, maps of maps, you can tell that I learned PHP before Java ;o)
	private Map<EntityType, Map<Long, Entity>> entities;

	private IdMapper idMapper = new DefaultIdMapper();
	
	//
	// INITIALISATION
	//

	// Check that all required fields are present
	@Override
	protected void doInitialise() {
		requireNotNull(classes, "classes");
		requireNotNull(typeDomainId, "typeDomainId");
		requireNotNull(objectGraphId, "objectGraphId");
		requireNotNull(idMapper, "idMapper");
		
		if (edrMapper == null) {
			DefaultEDRMapper defaultMapper = new DefaultEDRMapper();
			defaultMapper.setTypeDomainId(typeDomainId);
			defaultMapper.setClasses(classes);
			defaultMapper.initialise();
			edrMapper = defaultMapper;
		}
		
		typeDomain = edrMapper.getTypeDomain();
		entities = new HashMap<EntityType, Map<Long, Entity>>();
		for (EntityType entityType: typeDomain.getEntityTypes()) {
			entities.put(entityType, new HashMap<Long, Entity>());
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
	 * Provide an alternative {@link EDRMapper}.
	 * 
	 * If used at all, this method must be called before initialise(). If no alternative
	 * {@link EDRMapper} is provided, {@link DefaultEDRMapper} will be used
	 */
	public void setTypeDomainMapper(EDRMapper typeDomainMapper) {
		requireInitialised(false, "setTypeDomainMapper()");
		this.edrMapper = typeDomainMapper;
	}

	/**
	 * Provide an alternative {@link IdMapper}.
	 * 
	 * If used at all, this method must be called before initialise(). If no alternative
	 * {@link IdMapper} is provided, {@link DefaultIdMapper} will be used
	 */
	public void setIdMapper(IdMapper idMapper) {
		requireInitialised(false, "setIdMapper()");
		this.idMapper = idMapper;
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

	@Override
	public ObjectGraph createSnapshot() {
		//TODO design EntityStore for this and ClientEngine's purposes
		List<Entity> entityList = new ArrayList<Entity>();
		for (Map<Long, Entity> map: entities.values()) {
			for (Entity e: map.values()) {
				entityList.add(e);
			}
		}
		return new ImmutableObjectGraph(typeDomain, objectGraphId, entityList.toArray(new Entity[0]));
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
			long id = idMapper.getId(entityObject);
			Entity entity = edrMapper.createEntity(entityObject, id);
			entities.get(entity.getEntityType()).put(entity.getId(), entity);
		}
	}

}
