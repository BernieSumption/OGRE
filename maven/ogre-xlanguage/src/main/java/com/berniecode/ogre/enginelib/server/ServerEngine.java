package com.berniecode.ogre.enginelib.server;

import com.berniecode.ogre.enginelib.platformhooks.NativeStringMap;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.StringMap;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * A ServerEngine provides access to any number of object graphs belonging to any number of type
 * domains
 * 
 * @author Bernie Sumption
 */
public class ServerEngine {

	private DataSource[] dataAdapters;
	private boolean initialised = false;
	
	// A map of type domain id to TypeDomain object
	private StringMap typeDomains = new NativeStringMap();
	

	/**
	 * @return A type domain managed by this server engine
	 * 
	 * @throws NoSuchThingException if this server engine does not manage the specified type domain
	 */
	public TypeDomain getTypeDomainById(String typeDomainId) throws NoSuchThingException {
		requireInitialised(true, "getTypeDomainById()");
		if (typeDomains.contains(typeDomainId)) {
			return (TypeDomain) typeDomains.get(typeDomainId);
		}
		throw new NoSuchThingException("This ServerEngine has no type domain with ID '" + typeDomainId + "'");
	}

	/**
	 * Convenience method used to set a single object graph used by this engine.
	 */
	public void setDataAdapter(DataSource dataAdapter) {
		requireInitialised(false, "setDataAdapter()");
		DataSource[] dataAdapters = new DataSource[1];
		dataAdapters[0] = dataAdapter;
		setDataAdapters(dataAdapters);
	}

	/**
	 * Set the set of object graphs used by this engine.
	 * 
	 * <p>Must be called before 
	 */
	public void setDataAdapters(DataSource[] dataAdapters) {
		requireInitialised(false, "setDataAdapters()");
		this.dataAdapters = dataAdapters;
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
		requireNotNull(dataAdapters, "dataAdapters");
		for (int i = 0; i < dataAdapters.length; i++) {
			DataSource dataAdapter = dataAdapters[i];
			TypeDomain typeDomain = dataAdapter.getTypeDomain();
			typeDomains.put(typeDomain.getTypeDomainId(), typeDomain);
		}
	}

	//
	// PRIVATE MACHINERY
	//

	private void requireNotNull(Object required, String name) {
		if (required == null) {
			throw new OgreException("A value for " + name + " must be supplied before initialise() is called.");
		}
	}

	private void requireInitialised(boolean requiredStatus, String methodName) {
		if (initialised != requiredStatus) {
			throw new OgreException(methodName + " can't be called " + (requiredStatus ? "before" : "after")
					+ " initialise()");
		}
	}

}
