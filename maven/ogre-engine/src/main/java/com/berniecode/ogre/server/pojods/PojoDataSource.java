package com.berniecode.ogre.server.pojods;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.NativeSimpleList;
import com.berniecode.ogre.enginelib.server.DataSource;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;
import com.berniecode.ogre.enginelib.shared.Entity;
import com.berniecode.ogre.enginelib.shared.EntityStore;
import com.berniecode.ogre.enginelib.shared.ObjectGraph;
import com.berniecode.ogre.enginelib.shared.ObjectGraphSnapshot;
import com.berniecode.ogre.enginelib.shared.SimpleList;
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
	private IdMapper idMapper = new DefaultIdMapper();
	private UpdateMessageListener updateMessageListener;


	private TypeDomain typeDomain;
	private EntityStore entities = new EntityStore();
	
	//
	// INITIALISATION
	//

	// Check that all required fields are present
	@Override
	protected void doInitialise() {
		requireNotNull(edrMapper, "edrMapper");
		requireNotNull(objectGraphId, "objectGraphId");
		requireNotNull(idMapper, "idMapper");
		
		typeDomain = edrMapper.getTypeDomain();
		
		if (OgreLog.isDebugEnabled()) {
			OgreLog.debug("PojoDataSource created new type domain:\n" + EDRDescriber.describeTypeDomain(typeDomain));
		}
	}

	/**
	 * Provide an alternative {@link EDRMapper}.
	 * 
	 * If used at all, this method must be called before initialise(). If no alternative
	 * {@link EDRMapper} is provided, {@link DefaultEDRMapper} will be used,
	 */
	public void setEDRMapper(EDRMapper edrMapper) {
		requireInitialised(false, "setTypeDomainMapper()");
		this.edrMapper = edrMapper;
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
		return new ObjectGraphSnapshot(typeDomain, objectGraphId, entities.getAllEntities());
	}

	@Override
	public void setUpdateMessageListener(UpdateMessageListener listener) {
		updateMessageListener = listener;
	}
	
	//
	// PUBLIC API
	//

	/**
	 * Set the contents of the object graph.
	 * 
	 * <ul>
	 * <li>Any objects that are not part of this graph will be added, and broadcast to clients
	 * <li>Any objects that are already part of this graph will be checked for modifications, and
	 * any changed property values will be broadcast to clients
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
	public void setEntityObjects(Object ... entityObjects) throws ValueMappingException {
		requireInitialised(true, "setEntityObjects()");
		SimpleList completeEntities = new NativeSimpleList();
		for (int i=0; i<entityObjects.length; i++) {
			long id = idMapper.getId(entityObjects[i]);
			Entity newEntity = edrMapper.createEntity(entityObjects[i], id);
			Entity similar = entities.getSimilar(newEntity);
			if (similar == null) {
				completeEntities.add(newEntity);
				entities.addNew(newEntity);
			} else {
				//TODO implement entity updates here
				completeEntities.add(newEntity);
				entities.replace(newEntity);
			}
		}
		sendUpdateMessage(completeEntities);
	}

	private void sendUpdateMessage(SimpleList completeEntities) {
		Entity[] newEntitiesArr = new Entity[completeEntities.size()];
		completeEntities.copyToArray(newEntitiesArr);
		if (updateMessageListener != null) {
			updateMessageListener.acceptUpdateMessage(new UpdateMessage(
					typeDomain.getTypeDomainId(), objectGraphId, newEntitiesArr));
		}
	}

	/**
	 * Check whether an object is currently part of the object graph
	 */
	public boolean containsEntityObject(Object entityObject) {
		return idMapper.hasId(entityObject);
	}

	/**
	 * Return the ID for an object. Dependng on the {@link IdMapper} used, this may actually cause
	 * a new ID to be assigned to the object.
	 */
	public long getIdForObject(Object entityObject) {
		return idMapper.getId(entityObject);
	}

}
