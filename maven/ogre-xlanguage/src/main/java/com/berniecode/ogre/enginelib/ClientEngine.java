package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A ClientEngineTest configures and executes the replication of a single object graph. It is the
 * frontend of the cross-language OGRE client, and will typically not be used directly but should be
 * wrapped in a suitable language-specific facade.
 * 
 * @author Bernie Sumption
 */
public class ClientEngine implements GraphUpdateListener {

	private DownloadClientAdapter downloadAdapter;
	private String typeDomainId;
	private String objectGraphId;
	private boolean initialised = false;

	private TypeDomain typeDomain;

	private EntityStore entities;
	private MessageClientAdapter messageAdapter;

	/**
	 * Set the type domain used by this engine. This must be called before the engine is
	 * initialised, and can't be called again after initialisation
	 */
	public void setTypeDomainId(String typeDomainId) {
		requireInitialised(false, "setTypeDomainId()");
		this.typeDomainId = typeDomainId;
	}

	/**
	 * Set the type domain used by this engine. This must be called before the engine is
	 * initialised, and can't be called again after initialisation
	 */
	public void setObjectGraphId(String objectGraphId) {
		requireInitialised(false, "setObjectGraphId()");
		this.objectGraphId = objectGraphId;
	}

	/**
	 * Set the {@link DownloadClientAdapter} used by this engine. This must be called before the
	 * engine is initialised, and can't be called again after initialisation
	 */
	public void setDownloadAdapter(DownloadClientAdapter adapter) {
		requireInitialised(false, "setDownloadAdapter()");
		this.downloadAdapter = adapter;
	}

	/**
	 * Set the {@link MessageClientAdapter} used by this engine. This must be called before the
	 * engine is initialised, and can't be called again after initialisation
	 */
	public void setMessageAdapter(MessageClientAdapter adapter) {
		requireInitialised(false, "setMessageAdapter()");
		this.messageAdapter = adapter;
	}

	/**
	 * Initialise the client engine. All required components must have been provided.
	 * 
	 * @throws OgreException if the required dependencies have not been supplied
	 * @throws IOFailureException if the OGRE server could not be contacted
	 * @throws NoSuchThingException if the OGRE server does not have a TypeDomain with the
	 *             specified ID
	 */
	public void initialise() throws NoSuchThingException {
		if (initialised) {
			return;
		}
		requireNotNull(downloadAdapter, "downloadAdapter");
		requireNotNull(messageAdapter, "messageAdapter");
		requireNotNull(typeDomainId, "typeDomainId");
		requireNotNull(objectGraphId, "objectGraphId");
		
		typeDomain = downloadAdapter.loadTypeDomain(typeDomainId);
		entities = new EntityStore(typeDomain);
		initialised = true;
		
		acceptGraphUpdate(downloadAdapter.loadObjectGraph(typeDomain, objectGraphId));
		
		messageAdapter.subscribeToGraphUpdates(typeDomain, objectGraphId, this);
		
	}

	/**
	 * @return the TypeDomain used by this client engine.
	 * 
	 * @throws OgreException if the client engine has not been initialised yet
	 */
	public TypeDomain getTypeDomain() {
		requireInitialised(true, "getTypeDomain()");
		return typeDomain;
	}

	//
	// PRIVATE MACHINERY
	//

	private void requireNotNull(Object required, String name) {
		if (required == null) {
			throw new InitialisationException("A value for " + name + " must be supplied before initialise() is called.");
		}
	}

	private void requireInitialised(boolean requiredStatus, String methodName) {
		if (initialised != requiredStatus) {
			throw new InitialisationException(methodName + " can't be called " + (requiredStatus ? "before" : "after")
					+ " initialise()");
		}
	}

	/**
	 * @private
	 */
	public void acceptGraphUpdate(GraphUpdate update) {
//		requireInitialised(true, "acceptGraphUpdate()");
//		
//		// decide what Entities we're going to add
//		ArrayBuilder builder = new ArrayBuilder(Entity.class);
//		RawPropertyValueSet[] completeValues = update.getEntityValues();
//		for (int i = 0; i < completeValues.length; i++) {
//			if (!entities.containsSimilar(completeValues[i])) {
//				
//			}
//		}
//		
//		// wire up the entity references
//		Entity[] completeEntities = update.getEntityValues();
//		for (int i = 0; i < completeEntities.length; i++) {
//			completeEntities[i].wireEntityReferences(entities, completeEntities);
//		}
//		
//		OgreLog.info("ClientEngine: accepted graph update " + update);
//		if (OgreLog.isDebugEnabled()) {
//			OgreLog.debug(EDRDescriber.describeGraphUpdate(update));
//		}
//		mergeCompleteEntities(completeEntities);
//		mergeEntityDiffs(update.getEntityDiffs());
//		mergeEntityDeletes(update.getEntityDeletes());
	}

	/**
	 * Merge a number of entities into this engine. For each entity, if the engine already contains
	 * an entity with the same type and id, the existing entity will be updated with values from the
	 * new entity. Otherwise, the new entity will be added to this engine.
	 */
	private void mergeEntityValues(Entity[] completeEntities) {
		for (int i=0; i<completeEntities.length; i++) {
			Entity entity = completeEntities[i];
			Entity existingEntity = entities.getSimilar(entity);
			if (existingEntity != null) {
				if (OgreLog.isInfoEnabled()) {
					OgreLog.info("ClientStore: replacing entity " + existingEntity + " with new complete entity value " + entity);
				}
				existingEntity.update(entity);
			} else {
				if (OgreLog.isInfoEnabled()) {
					OgreLog.info("ClientStore: adding new entity " + entity);
				}
				entities.add(entity);
			}
		}
	}

	/**
	 * Merge a number of {@link EntityDiff} objects
	 */
	private void mergeEntityDiffs(EntityDiff[] entityDiffs) {
		for (int i=0; i<entityDiffs.length; i++) {
			EntityDiff diff = entityDiffs[i];
			Entity target = entities.getSimilar(diff);
			if (target == null) {
				OgreLog.error("ClientEngine: received diff '" + diff + "' but there is no local entity of the same ID and type to apply it to");
			} else {
				if (OgreLog.isInfoEnabled()) {
					OgreLog.info("ClientStore: updating values of " + target + " due to " + diff);
				}
				target.update(diff);
			}
		}
	}

	private void mergeEntityDeletes(EntityDelete[] entityDeletes) {
		for (int i=0; i<entityDeletes.length; i++) {
			EntityDelete entityDelete = entityDeletes[i];
			Entity target = entities.getSimilar(entityDelete);
			if (target == null) {
				OgreLog.error("ClientEngine: received delete '" + entityDelete + "' but there is no local entity of the same ID and type to apply it to");
			} else {
				if (OgreLog.isInfoEnabled()) {
					OgreLog.info("ClientStore: deleting entity " + target + " due to " + entityDelete);
				}
				entities.removeSimilar(target);
			}
		}
	}

	/**
	 * The values array passed to the constructor of this class contains integers instead of Entity
	 * references, so if property #0 is a "reference to Foo" property referencing Foo#7,
	 * getPropertyValue(property0) would return the number "7".
	 * 
	 * <p>
	 * This method is used to provide a set of Entities to resolve references in, so that
	 * getPropertyValue(property0) returns the actual Entity Foo#7
	 * 
	 * <p>
	 * Entities are resolved first in the EntityStore, then in the array of entities if they are not
	 * found in the store
	 * 
	 * @private
	 */
	void wireEntityReferences(EntityStore store, Entity[] array) {
//		ReferenceProperty[] properties = entityType.getReferenceProperties();
//		for (int i = 0; i < properties.length; i++) {
//			ReferenceProperty property = properties[i];
//			EntityType refType = property.getReferenceType();
//			Object value = values[property.getPropertyIndex()];
//			if (value != null) {
//				long refId = ValueUtils.unboxLong((Long) value);
//				Entity entity = null;
//				if (store != null) {
//					entity = store.get(refType, refId);
//				}
//				if (entity == null && array != null) {
//					for (int j = 0; j < array.length; j++) {
//						if (array[j].getEntityType() == refType && array[j].getEntityId() == refId) {
//							entity = array[j];
//						}
//					}
//				}
//				if (entity == null) {
//					throw new OgreException("Property '" + property + "' of entity type " + property.getEntityType() + " references non-existant entity " + refType + "#" + refId);
//				}
//				values[property.getPropertyIndex()] = entity;
//			}
//		}
	}
	

	/**
	 * @return a snapshot of the state of this object graph, useful for debugging
	 */
	public GraphUpdate createSnapshot() {
		requireInitialised(true, "createSnapshot()");
		return new GraphUpdate(typeDomain, objectGraphId, entities.getEntities(), null, null);
	}
}
