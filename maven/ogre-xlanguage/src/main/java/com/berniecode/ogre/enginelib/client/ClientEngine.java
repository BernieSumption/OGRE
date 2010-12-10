package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.IOFailureException;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityDiff;
import com.berniecode.ogre.enginelib.shared.EntityStore;
import com.berniecode.ogre.enginelib.shared.EntityValue;
import com.berniecode.ogre.enginelib.shared.ObjectGraphValue;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.enginelib.shared.UpdateMessage;
import com.berniecode.ogre.enginelib.shared.UpdateMessageListener;

/**
 * A ClientEngineTest configures and executes the replication of a single object graph. It is the
 * frontend of the cross-language OGRE client, and will typically not be used directly but should be
 * wrapped in a suitable language-specific facade.
 * 
 * @author Bernie Sumption
 */
public class ClientEngine implements UpdateMessageListener {

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

	public String getObjectGraphId() {
		return objectGraphId;
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
	public void initialise() throws NoSuchThingException, IOFailureException {
		if (initialised) {
			return;
		}
		requireNotNull(downloadAdapter, "downloadAdapter");
		requireNotNull(messageAdapter, "messageAdapter");
		requireNotNull(typeDomainId, "typeDomainId");
		requireNotNull(objectGraphId, "objectGraphId");
		typeDomain = downloadAdapter.loadTypeDomain(typeDomainId);
		
		entities = new EntityStore(typeDomain);
		
		ObjectGraphValue objectGraph = downloadAdapter.loadObjectGraph(typeDomain, objectGraphId);
		EntityValue[] initialValues = objectGraph.getEntityValues();
		for (int i=0; i<initialValues.length; i++) {
			entities.addNew(initialValues[i].toEntity(typeDomain));
		}
		
		messageAdapter.subscribeToUpdateMessages(typeDomainId, objectGraphId, this);
		
		initialised = true;
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

	/**
	 * @return an array of all Entities of a specified entity type
	 */
	public Entity[] getEntities() {
		return entities.getAllEntities();
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
	public void acceptUpdateMessage(UpdateMessage message) {
		OgreLog.info("Accepted update message " + message);
		//TODO describe and log update message
		mergeCompleteEntities(message.getEntityValues());
		mergeEntityDiffs(message.getEntityDiffs());
	}

	/**
	 * Merge a number of entities into this engine. For each entity, if the engine already contains
	 * an entity with the same type and id, the existing entity will be updated with values from the
	 * new entity. Otherwise, the new entity will be added to this engine.
	 */
	void mergeCompleteEntities(EntityValue[] entityValues) {
		for (int i=0; i<entityValues.length; i++) {
			EntityValue entityValue = entityValues[i];
			Entity existingEntity = entities.getSimilar(entityValue);
			if (existingEntity != null) {
				if (OgreLog.isInfoEnabled()) {
					OgreLog.info("ClientStore: replacing entity " + entityValue + " with new complete entity");
				}
				existingEntity.updateFromEntityValue(entityValue);
			} else {
				if (OgreLog.isInfoEnabled()) {
					OgreLog.info("ClientStore: adding new entity " + entityValue);
				}
				entities.addNew(entityValue.toEntity(typeDomain));
			}
		}
	}

	/**
	 * Merge a number of {@link EntityDiff} objects
	 */
	private void mergeEntityDiffs(EntityDiff[] entityDiffs) {
		for (int i=0; i<entityDiffs.length; i++) {
			EntityDiff diff = entityDiffs[i];
			Entity target = entities.get(diff.getEntityType(), diff.getId());
			if (target == null) {
				OgreLog.error("ClientEngine: received diff '" + diff + "' but there is no local entity of the same ID and type to apply it to");
			} else {
				if (OgreLog.isInfoEnabled()) {
					OgreLog.info("ClientStore: updating values of " + target);
				}
				target.updateFromEntityDiff(diff);
			}
		}
	}
	

	/**
	 * @return a snapshot of the state of this object graph, useful for debugging
	 */
	public ObjectGraphValue createSnapshot() {
		Entity[] allEntities = entities.getAllEntities();
		EntityValue[] entityValues = new EntityValue[allEntities.length];
		for (int i=0; i<allEntities.length; i++) {
			entityValues[i] = EntityValue.build(allEntities[i]);
		}
		return new ObjectGraphValue(typeDomain.getTypeDomainId(), objectGraphId, entityValues);
	}
}
