package com.berniecode.ogre.server.pojods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.server.DataSource;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityDeleteMessage;
import com.berniecode.ogre.enginelib.shared.EntityDiffMessage;
import com.berniecode.ogre.enginelib.shared.EntityStore;
import com.berniecode.ogre.enginelib.shared.EntityValueMessage;
import com.berniecode.ogre.enginelib.shared.ObjectGraphValueMessage;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.enginelib.shared.UpdateMessage;
import com.berniecode.ogre.enginelib.shared.UpdateMessageListener;

/**
 * A {@link DataSource} that extracts a {@link TypeDomain} from a set of java classes and an
 * {@link ObjectGraph} from from a set of java objects
 * 
 * @author Bernie Sumption
 */
public class PojoDataSource extends InitialisingBean implements DataSource {

	private EDRMapper edrMapper;
	private String objectGraphId;
	private UpdateMessageListener updateMessageListener;


	private TypeDomain typeDomain;
	private EntityStore entities;
	
	//
	// INITIALISATION
	//

	// Check that all required fields are present
	@Override
	protected void doInitialise() {
		requireNotNull(edrMapper, "edrMapper");
		requireNotNull(objectGraphId, "objectGraphId");
		
		typeDomain = edrMapper.getTypeDomain();
		entities = new EntityStore(typeDomain, true);
		
		if (OgreLog.isDebugEnabled()) {
			OgreLog.debug("PojoDataSource created new type domain:\n" + EDRDescriber.describeTypeDomain(typeDomain));
		}
	}

	/**
	 * Provide an {@link EDRMapper}.
	 */
	public void setEDRMapper(EDRMapper edrMapper) {
		requireInitialised(false, "setTypeDomainMapper()");
		this.edrMapper = edrMapper;
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
	public ObjectGraphValueMessage createSnapshot() {
		return new ObjectGraphValueMessage(typeDomain.getTypeDomainId(), objectGraphId, entities.createEntityValueMessages());
	}

	@Override
	public void setUpdateMessageListener(UpdateMessageListener listener) {
		updateMessageListener = listener;
	}
	
	//
	// PUBLIC API
	//

	/**
	 * Set the contents of the object graph. The objects passed in to this method are a <strong>set
	 * of root objects</strong> for the object graph. The object graph is then traversed from these
	 * roots, and any other objects directly or indirectly referenced by a root object will be
	 * considered part of the object graph.
	 * 
	 * <ul>
	 * <li>Any objects that are not part of this graph will be added, and a
	 * {@link EntityValueMessage} will be broadcast to clients
	 * <li>Any objects that are already part of this graph will be checked for modifications, and a
	 * {@link EntityDiffMessage} will be broadcast to clients
	 * <li>Any objects in the graph that are not in the array passed to this method will be removed
	 * from the graph.
	 * </ul>
	 * 
	 * <p>
	 * Each object must be an instance of one of the classes passed to {@link #setClasses(Class...)}
	 * 
	 * <p>
	 * {@link #initialise()} must be called before this method can be used
	 * 
	 * @throws ValueMappingException if there is a problem mapping one of the entity objects to an
	 *             {@link Entity}
	 */
	public void setEntityObjects(Object ... objectGraphRoots) throws ValueMappingException {
		requireInitialised(true, "setEntityObjects()");
		
		Set<Object> entityObjects = new LinkedHashSet<Object>(Arrays.asList(objectGraphRoots));
		
		// iteratively grow the set of entity objects to include any objects referenced by any other objects
		// in the set. This algorithm depends heavily on the behaviour of LinkedHashMap.addAll()
		{
			int processedUpTo = -1;
			while (entityObjects.size() - 1 > processedUpTo) {
				int currentIndex = 0;
				List<Object> toAdd = new ArrayList<Object>();
				for (Object entityObject: entityObjects) {
					if (currentIndex > processedUpTo) { 
						toAdd.addAll(edrMapper.getRelatedObjects(entityObject));
						processedUpTo = currentIndex;
					}
					currentIndex ++;
				}
				entityObjects.addAll(toAdd); 
			}
		}
		
		//for each 
		List<EntityValueMessage> completeEntities = new ArrayList<EntityValueMessage>();
		List<EntityDiffMessage> entityDiffs = new ArrayList<EntityDiffMessage>();
		List<EntityDeleteMessage> entityDeletes = new ArrayList<EntityDeleteMessage>();
		List<Entity> newEntities = new ArrayList<Entity>();
		for (Object entityObject: entityObjects) {
			Entity newEntity = edrMapper.createEntity(entityObject);
			Entity similar = entities.getSimilar(newEntity);
			if (similar == null) {
				completeEntities.add(EntityValueMessage.build(newEntity));
			} else {
				EntityDiffMessage diff = EntityDiffMessage.build(similar, newEntity);
				if (diff != null) {
					if (OgreLog.isInfoEnabled()) {
						OgreLog.info("PojDataSource: detected change on " + similar);
					}
					entityDiffs.add(diff);
				}
			}
			entities.put(newEntity);
			newEntities.add(newEntity);
		}
		
		for (Entity oldEntity: entities.getEntities()) {
			if (!newEntities.contains(oldEntity)) {
				entities.removeSimilar(oldEntity);
				entityDeletes.add(EntityDeleteMessage.build(oldEntity));
			}
		}
		
		sendUpdateMessage(completeEntities, entityDiffs, entityDeletes);
	}

	private void sendUpdateMessage(
			List<EntityValueMessage> newEntities,
			List<EntityDiffMessage> entityDiffs,
			List<EntityDeleteMessage> entityDeletes) {
		
		if (newEntities.size() == 0 && entityDiffs.size() == 0 && entityDeletes.size() == 0) {
			return;
		}
		
		if (updateMessageListener != null) {
			UpdateMessage message = new UpdateMessage(
					typeDomain.getTypeDomainId(),
					objectGraphId,
					newEntities.toArray(new EntityValueMessage[0]),
					entityDiffs.toArray(new EntityDiffMessage[0]),
					entityDeletes.toArray(new EntityDeleteMessage[0]));
			if (OgreLog.isDebugEnabled()) {
				OgreLog.debug("PojoDataSource: broadcasting new update message:\n"
						+ EDRDescriber.describeUpdateMessage(typeDomain, message));
			}
			updateMessageListener.acceptUpdateMessage(message);
		}
	}

	/**
	 * Check whether an object is currently part of the object graph
	 */
	public boolean containsEntityObject(Object entityObject) {
		return edrMapper.objectHasId(entityObject);
	}

	/**
	 * Return the ID for an object. Dependng on the {@link IdMapper} used, this may actually cause
	 * a new ID to be assigned to the object.
	 */
	public long getIdForObject(Object entityObject) {
		return edrMapper.getIdForObject(entityObject);
	}

}
