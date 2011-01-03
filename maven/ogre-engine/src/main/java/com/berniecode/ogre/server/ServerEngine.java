package com.berniecode.ogre.server;

import java.util.HashMap;
import java.util.Map;

import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.server.ServerEngineTest;

/**
 * A {@link ServerEngine} contains a number of object graphs
 * 
 * <p>
 * Each object graph is managed by a {@link DataSource}.
 * 
 * @author Bernie Sumption
 */
public class ServerEngine implements GraphUpdateListener {

	private DataSource[] dataSources;
	private MessageServerAdapter messageAdapter;
	
	private boolean initialised = false;
	
	// A map of type domain id to TypeDomain object
	private Map<String, TypeDomain> typeDomains = new HashMap<String, TypeDomain>();
	
	//
	// INITIALISATION
	//

	/**
	 * Convenience method used to set a single object graph used by this engine.
	 */
	public void setDataSource(DataSource dataAdapter) {
		requireInitialised(false, "setDataAdapter()");
		DataSource[] dataAdapters = new DataSource[1];
		dataAdapters[0] = dataAdapter;
		setDataSources(dataAdapters);
	}

	/**
	 * Set the set of object graphs used by this engine.
	 * 
	 * <p>Must be called before {@link #initialise()}
	 */
	public void setDataSources(DataSource[] dataAdapters) {
		requireInitialised(false, "setDataAdapters()");
		this.dataSources = dataAdapters;
	}

	/**
	 * Provide a {@link MessageServerAdapter} to send graph updates to clients
	 * 
	 * <p>Must be called before {@link #initialise()}
	 */
	public void setMessageAdapter(MessageServerAdapter messageAdapter) {
		this.messageAdapter = messageAdapter;
	}
	
	/**
	 * Check dependencies and start the server engine.
	 * 
	 * @throws OgreException if the dependencies have not been provided
	 */
	public void initialise() throws OgreException {
		if (initialised) {
			return;
		}
		initialised = true;
		requireNotNull(dataSources, "dataSources");
		requireNotNull(messageAdapter, "messageAdapter");
		for (int i = 0; i < dataSources.length; i++) {
			DataSource dataSource = dataSources[i];
			TypeDomain typeDomain = dataSource.getTypeDomain();
			String tdId = typeDomain.getTypeDomainId();
			if (typeDomains.containsKey(tdId) && typeDomains.get(tdId) != typeDomain) {
				throw new OgreException("Two DataSource objects provide the same type domain id ('"+ tdId +
						"') but the TypeDomain objects returned from DataSource.getTypeDomain() are different.");
			}
			typeDomains.put(tdId, typeDomain);
			dataSource.setGraphUpdateListener(this);
		}
	}
	
	//
	// PUBLIC API
	//
	

	/**
	 * @return A type domain managed by this {@link ServerEngineTest}
	 * 
	 * @throws NoSuchThingException if this {@link ServerEngineTest} does not manage the specified type domain
	 */
	public TypeDomain getTypeDomain(String typeDomainId) throws NoSuchThingException {
		requireInitialised(true, "getTypeDomainById()");
		if (typeDomains.containsKey(typeDomainId)) {
			return typeDomains.get(typeDomainId);
		}
		throw new NoSuchThingException("This ServerEngineTest has no type domain with ID '" + typeDomainId + "'");
	}

	/**
	 * @return An object graph managed by this {@link ServerEngineTest}
	 * 
	 * @throws NoSuchThingException if this {@link ServerEngineTest} does not manage the specified type
	 *             domain or object graph
	 */
	public GraphUpdate getObjectGraph(String typeDomainId, String objectGraphId) throws NoSuchThingException {
		requireInitialised(true, "getObjectGraph()");
		for (int i = 0; i < dataSources.length; i++) {
			String tdId = dataSources[i].getTypeDomain().getTypeDomainId();
			String ogId = dataSources[i].getObjectGraphId();
			if (tdId.equals(typeDomainId) && ogId.equals(objectGraphId)) {
				return dataSources[i].createSnapshot();
			}
		}
		throw new NoSuchThingException("This ServerEngineTest does not manage the object graph '"
				+ typeDomainId + "/" + objectGraphId + "'");
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
	@Override
	public void acceptGraphUpdate(GraphUpdate update) {
		messageAdapter.publishGraphUpdate(update);
	}

}
