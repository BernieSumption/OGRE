package com.berniecode.ogre.server.pojods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.server.DataSource;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityDelete;
import com.berniecode.ogre.enginelib.shared.EntityDiff;
import com.berniecode.ogre.enginelib.shared.EntityStore;
import com.berniecode.ogre.enginelib.shared.GraphUpdate;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.enginelib.shared.UpdateMessageListener;

/**
 * A {@link DataSource} that extracts a {@link TypeDomain} from a set of java classes and an
 * {@link ObjectGraph} from from a set of java objects.
 * 
 * <p>
 * Each time your data has changed (or on a regular schedule if you don't know when your data has
 * changed) you should call {@link #setEntityObjects(Object...)} passing in a set of objects that
 * form root nodes of the object graph. {@link PojoDataSource} will traverse the java object graph
 * from the provided root nodes and find any objects directly or indirectly referenced by the root
 * nodes.
 * 
 * <p>
 * {@link PojoDataSource} maintains a complete copy of the object graph data in memory, and uses
 * this to detect changes on any object. For this reason it will consume memory and CPU resources in
 * proportion to both the size of the object graph, and the frequency of calls to
 * {@link #setEntityObjects(Object...)}.
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
	public GraphUpdate createSnapshot() {
		Entity[] messages = entities.getEntities();
		Arrays.sort(messages, new EntityComparator());
		return new GraphUpdate(typeDomain.getTypeDomainId(), objectGraphId, messages, null, null);
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
	 * <li>Any objects that are not part of this graph will be added, and a complete {@link Entity}
	 * value will be broadcast to clients
	 * <li>Any objects that are already part of this graph will be checked for modifications, and a
	 * {@link EntityDiff} will be broadcast to clients
	 * <li>Any objects in the graph that are not in the array passed to this method will be removed
	 * from the graph and an {@link EntityDelete} broadcast to clients.
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
		// in the set. This algorithm depends heavily on the behaviour of LinkedHashSet.addAll(), that is that
		// objects already in the set are ignored, and new objects are added at the end of the list
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
		List<Entity> completeEntities = new ArrayList<Entity>();
		List<EntityDiff> entityDiffs = new ArrayList<EntityDiff>();
		List<EntityDelete> entityDeletes = new ArrayList<EntityDelete>();
		List<Entity> newEntities = new ArrayList<Entity>();
		for (Object entityObject: entityObjects) {
			Entity newEntity = edrMapper.createEntity(entityObject);
			Entity similar = entities.getSimilar(newEntity);
			if (similar == null) {
				completeEntities.add(newEntity);
			} else {
				EntityDiff diff = EntityDiff.build(similar, newEntity);
				if (diff != null) {
					if (OgreLog.isInfoEnabled()) {
						OgreLog.info("PojoDataSource: detected change on " + similar);
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
				entityDeletes.add(EntityDelete.build(oldEntity));
			}
		}
		
		sendUpdateMessage(completeEntities, entityDiffs, entityDeletes);
	}

	private void sendUpdateMessage(
			List<Entity> newEntities,
			List<EntityDiff> entityDiffs,
			List<EntityDelete> entityDeletes) {
		
		if (newEntities.size() == 0 && entityDiffs.size() == 0 && entityDeletes.size() == 0) {
			return;
		}
		
		if (updateMessageListener != null) {
			GraphUpdate message = new GraphUpdate(
					typeDomain.getTypeDomainId(),
					objectGraphId,
					newEntities.toArray(new Entity[0]),
					entityDiffs.toArray(new EntityDiff[0]),
					entityDeletes.toArray(new EntityDelete[0]));
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
	

	// sorts entities first by entityTypeId, then by entityId
	private final class EntityComparator implements Comparator<Entity> {
		@Override
		public int compare(Entity o1, Entity o2) {
			if (o1.getEntityType().getEntityTypeIndex() != o2.getEntityType().getEntityTypeIndex()) {
				return compareNumbers(o1.getEntityType().getEntityTypeIndex(), o2.getEntityType().getEntityTypeIndex());
			} else {
				return compareNumbers(o1.getEntityId(), o2.getEntityId());
			}
		}

		private int compareNumbers(long o1Id, long o2Id) {
			return (o1Id < o2Id ? -1 : (o1Id == o2Id ? 0 : 1));
		}
	}

}
