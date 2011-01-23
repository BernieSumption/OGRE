package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.ArrayBuilder;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.InvalidGraphUpdateException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A ClientEngine configures and executes the replication of a single object graph. It is the
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
	public void acceptGraphUpdate(GraphUpdate update) throws InvalidGraphUpdateException {
		requireInitialised(true, "acceptGraphUpdate()");

		
		OgreLog.info("ClientEngine: applying graph update " + update);
		if (OgreLog.isDebugEnabled()) {
			OgreLog.debug(EDRDescriber.describeGraphUpdate(update));
		}
		
		// pre-build the Entities that we're going to add
		RawPropertyValueSet[] completeValues = update.getEntityCreates();
		Entity[] newEntities = new Entity[completeValues.length];
		for (int i = 0; i < completeValues.length; i++) {
			RawPropertyValueSet value = completeValues[i];
			for (int j = 0; j < i; j++) {
				if (value.getEntityId() == completeValues[j].getEntityId()
						&& value.getEntityType() == completeValues[j].getEntityType()) {
					throw new InvalidGraphUpdateException("Ignoring " + update + " message because it contains a duplicate ID: " + completeValues[j].getEntityType() + "#" + completeValues[j].getEntityId());
				}
			}
			if (entities.containsSimilar(value)) {
				throw new InvalidGraphUpdateException("Ignoring " + update + " because it creates an entity that already exists in the client engine: " + value);
			} else {
				newEntities[i] = new Entity(value.getEntityType(), value.getEntityId(), null); // these entities have no initial values
			}
		}
		for (int i = 0; i < newEntities.length; i++) {
			newEntities[i].update(completeValues[i], entities, newEntities); // wire up values
		}
		for (int i = 0; i < newEntities.length; i++) {
			entities.add(newEntities[i]);
		}
		
		// apply entity updates
		PartialRawPropertyValueSet[] entityUpdates = update.getEntityUpdates();
		for (int i=0; i<entityUpdates.length; i++) {
			PartialRawPropertyValueSet entityUpdate = entityUpdates[i];
			Entity target = entities.getSimilar(entityUpdate);
			if (target == null) {
				OgreLog.error("ClientEngine: received diff '" + entityUpdate + "' but there is no local entity of the same ID and type to apply it to");
			} else {
				if (OgreLog.isInfoEnabled()) {
					OgreLog.info("ClientStore: updating values of " + target + " due to " + entityUpdate);
				}
				target.update(entityUpdate, entities, newEntities);
			}
		}
		
		
		// apply entity deletes
		EntityReference[] entityDeletes = update.getEntityDeletes();
		for (int i=0; i<entityDeletes.length; i++) {
			EntityReference entityDelete = entityDeletes[i];
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
	 * @return All {@link Entity} of the specified entity type
	 */
	public Entity[] getEntitiesByType(EntityType entityType) {
		return entities.getEntitiesByType(entityType);
	}

	/**
	 * @return a single {@link Entity} specified by type and id, or null if there is no such
	 *         {@link Entity}
	 */
	public Entity getEntityByTypeAndId(EntityType entityType, long id) {
		return entities.get(entityType, id);
	}

	/**
	 * @return a snapshot of the state of this object graph, useful for debugging
	 */
	public GraphUpdate createSnapshot() {
		requireInitialised(true, "createSnapshot()");
		return new GraphUpdate(typeDomain, objectGraphId, entities.getEntities(), null, null);
	}
	
	public String toString() {
		return "ClientEngine " + typeDomainId + "/" + objectGraphId;
	}


	/**
	 * Return the entities that reference the specified {@link Entity} through the specified {@link ReferenceProperty}
	 * 
	 * @throws OgreException if {@code property.getReferenceType() != entity.getEntityType()}
	 */
	public Entity[] getReferencesTo(Entity entity, ReferenceProperty property) {
		if (property.getReferenceType() != entity.getEntityType()) {
			throw new OgreException(property + " does not reference the EntityType " + entity.getEntityType());
		}
		ArrayBuilder ab = new ArrayBuilder(Entity.class);
		Entity[] candidates = entities.getEntitiesByType(property.getEntityType());
		for (int i = 0; i < candidates.length; i++) {
			Entity target = (Entity) candidates[i].getPropertyValue(property);
			if (target == entity) {
				ab.add(candidates[i]);
			}
		}
		return (Entity[]) ab.buildArray();
	}
}
