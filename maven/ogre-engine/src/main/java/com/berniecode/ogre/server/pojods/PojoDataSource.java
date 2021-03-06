/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.server.pojods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.DataSource;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityDiff;
import com.berniecode.ogre.enginelib.EntityReference;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.EntityValue;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.UnsafeAccess;
import com.berniecode.ogre.server.EDRMapper;
import com.berniecode.ogre.server.IdMapper;

/**
 * A {@link DataSource} that extracts a {@link TypeDomain} from a set of java classes and an object
 * graph from from a set of java objects.
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
	private GraphUpdateListener graphUpdateListener;
	private int dataVersion;
	private int dataVersionScheme;

	private TypeDomain typeDomain;
	private Map<EntityType, Map<Long, EntityValue>> entities;

	//
	// INITIALISATION
	//

	// Check that all required fields are present
	@Override
	protected void doInitialise() {
		requireNotNull(edrMapper, "edrMapper");
		requireNotNull(objectGraphId, "objectGraphId");

		dataVersionScheme = new Random().nextInt();

		typeDomain = edrMapper.getTypeDomain();
		entities = new HashMap<EntityType, Map<Long, EntityValue>>();
		for (EntityType entityType : UnsafeAccess.getEntityTypes(typeDomain)) {
			entities.put(entityType, new HashMap<Long, EntityValue>());
		}

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
	public synchronized GraphUpdate createSnapshot() {
		return new GraphUpdate(typeDomain, objectGraphId, dataVersion, dataVersionScheme, getAllEntities().toArray(
				new EntityValue[0]), null, null);
	}

	@Override
	public void setGraphUpdateListener(GraphUpdateListener listener) {
		graphUpdateListener = listener;
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
	 * <p>
	 * Each root can either be an object, or an array of objects, or a java.util.Collection of
	 * objects
	 * 
	 * <ul>
	 * <li>Any objects that are not part of this graph will be added
	 * <li>Any objects that are already part of this graph will be checked for modifications
	 * <li>Any objects in the graph that are not in the array passed to this method will be removed
	 * from the graph
	 * </ul>
	 * 
	 * <p>
	 * {@link #initialise()} must be called before this method can be used
	 * 
	 * @throws ValueMappingException if there is a problem mapping one of the entity objects to an
	 *             {@link Entity}
	 */
	public synchronized void setEntityObjects(Object... roots) throws ValueMappingException {
		requireInitialised(true, "setEntityObjects()");

		Set<Object> entityObjects = new LinkedHashSet<Object>();

		// flatten arrays and collections
		for (Object root : roots) {
			if (root == null) {
				continue;
			}
			if (root instanceof Collection) {
				entityObjects.addAll((Collection<?>) root);
			} else if (root instanceof Object[]) {
				Object[] arr = (Object[]) root;
				for (int i = 0; i < arr.length; i++) {
					entityObjects.add(arr[i]);
				}
			} else {
				entityObjects.add(root);
			}
		}

		// iteratively grow the set of entity objects to include any objects referenced by any other
		// objects in the set. This algorithm depends on the behaviour of LinkedHashSet.addAll() -
		// that objects already in the set are ignored, and new objects are added at the end of the
		// list
		{
			int processedUpTo = -1;
			while (entityObjects.size() - 1 > processedUpTo) {
				int currentIndex = 0;
				List<Object> toAdd = new ArrayList<Object>();
				for (Object entityObject : entityObjects) {
					if (currentIndex > processedUpTo) {
						toAdd.addAll(edrMapper.getRelatedObjects(entityObject));
						processedUpTo = currentIndex;
					}
					currentIndex++;
				}
				entityObjects.addAll(toAdd);
			}
		}

		List<EntityValue> completeEntities = new ArrayList<EntityValue>();
		List<EntityDiff> entityDiffs = new ArrayList<EntityDiff>();
		List<EntityReference> entityDeletes = new ArrayList<EntityReference>();
		List<EntityValue> newEntities = new ArrayList<EntityValue>();
		for (Object entityObject : entityObjects) {
			EntityValue newEntity = edrMapper.createEntityValue(entityObject);
			EntityValue existingEntity = entities.get(newEntity.getEntityType()).get(newEntity.getEntityId());
			if (existingEntity == null) {
				completeEntities.add(newEntity);
			} else {
				EntityDiff diff = EntityDiff.build(existingEntity, newEntity);
				if (diff != null) {
					if (OgreLog.isInfoEnabled()) {
						OgreLog.info("PojoDataSource: detected change on " + existingEntity);
					}
					entityDiffs.add(diff);
				}
			}
			entities.get(newEntity.getEntityType()).put(newEntity.getEntityId(), newEntity);
			newEntities.add(newEntity);
		}

		for (EntityValue oldEntity : getAllEntities()) {
			if (!newEntities.contains(oldEntity)) {
				entities.get(oldEntity.getEntityType()).remove(oldEntity.getEntityId());
				entityDeletes.add(oldEntity);
			}
		}

		// send updates if required
		if (completeEntities.size() > 0 || entityDiffs.size() > 0 || entityDeletes.size() > 0) {

			dataVersion++;

			if (graphUpdateListener != null) {
				GraphUpdate update = new GraphUpdate(typeDomain, objectGraphId, dataVersion, dataVersionScheme,
						completeEntities.toArray(new EntityValue[0]), entityDiffs.toArray(new EntityDiff[0]),
						entityDeletes.toArray(new EntityReference[0]));
				graphUpdateListener.acceptGraphUpdate(update);
			}
		}
	}

	/**
	 * Check whether an object is currently part of the object graph
	 */
	public boolean containsEntityObject(Object entityObject) {
		return edrMapper.objectHasId(entityObject);
	}

	/**
	 * Return the ID for an object. Dependng on the {@link IdMapper} used, this may actually cause a
	 * new ID to be assigned to the object.
	 */
	public long getIdForObject(Object entityObject) {
		return edrMapper.getIdForObject(entityObject);
	}

	private List<EntityValue> getAllEntities() {
		List<EntityValue> entityList = new ArrayList<EntityValue>();
		for (Map<Long, EntityValue> map : entities.values()) {
			entityList.addAll(map.values());
		}
		return entityList;
	}

}
