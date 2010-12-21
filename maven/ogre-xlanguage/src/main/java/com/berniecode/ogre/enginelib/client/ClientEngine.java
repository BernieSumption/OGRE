package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityDelete;
import com.berniecode.ogre.enginelib.shared.EntityDiff;
import com.berniecode.ogre.enginelib.shared.EntityStore;
import com.berniecode.ogre.enginelib.shared.ObjectGraphUpdate;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
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
		
		entities = new EntityStore(typeDomain, false);
		
		ObjectGraphUpdate objectGraph = downloadAdapter.loadObjectGraph(typeDomainId, objectGraphId);
		Entity[] initialValues = objectGraph.getEntities();
		for (int i=0; i<initialValues.length; i++) {
			entities.put(initialValues[i]);
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
	public void acceptUpdateMessage(ObjectGraphUpdate message) {
		
		//TODO perform validation on the update message:
		// 1. check that the untyped Object[] values array is of the correct type
		// 2. check that the typeDomain is the same as our typeDomain
		
		requireInitialised(true, "acceptUpdateMessage()");
		OgreLog.info("ClientEngine: accepted update message " + message);
		if (OgreLog.isDebugEnabled()) {
			OgreLog.debug(EDRDescriber.describeUpdateMessage(typeDomain, message));
		}
		mergeCompleteEntities(message.getEntities());
		mergeEntityDiffs(message.getEntityDiffs());
		mergeEntityDeletes(message.getEntityDeletes());
		

		//TODO  move this to before ClientEngine state is compromised! refuse to accept messages that will leave it in a bad state
		entities.checkIntegrity();
	}

	/**
	 * Merge a number of entities into this engine. For each entity, if the engine already contains
	 * an entity with the same type and id, the existing entity will be updated with values from the
	 * new entity. Otherwise, the new entity will be added to this engine.
	 */
	private void mergeCompleteEntities(Entity[] completeEntities) {
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
				entities.put(entity);
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
	 * @return a snapshot of the state of this object graph, useful for debugging
	 */
	public ObjectGraphUpdate createSnapshot() {
		requireInitialised(true, "createSnapshot()");
		return new ObjectGraphUpdate(typeDomain.getTypeDomainId(), objectGraphId, entities.getEntities(), null, null);
	}
}
