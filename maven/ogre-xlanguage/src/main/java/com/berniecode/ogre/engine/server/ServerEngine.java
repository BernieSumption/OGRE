package com.berniecode.ogre.engine.server;

import com.berniecode.ogre.engine.NoSuchThingException;
import com.berniecode.ogre.engine.platformhooks.NativeStringMap;
import com.berniecode.ogre.engine.shared.StringMap;
import com.berniecode.ogre.engine.shared.TypeDomain;

/**
 * A ServerEngine provides access to any number of object graphs belonging to any number of type
 * domains
 * 
 * @author Bernie Sumption
 */
public class ServerEngine {

	// A map of type domain id to type domain
	private StringMap<TypeDomain> typeDomains = new NativeStringMap<TypeDomain>();

	/**
	 * @return A type domain managed by this server engine
	 * 
	 * @throws NoSuchThingException if this server engine does not manage the specified type domain
	 */
	public TypeDomain getTypeDomainById(String typeDomainId) throws NoSuchThingException {
		if (typeDomains.contains(typeDomainId)) {
			return typeDomains.get(typeDomainId);
		}
		throw new NoSuchThingException("This ServerEngine has no type domain with ID '" + typeDomainId + "'");
	}

	/**
	 * Convenience method used to set a single object graph used by this engine.
	 */
	public void setDataAdapter(DataSource dataAdapter) {
		DataSource[] dataAdapters = new DataSource[1];
		dataAdapters[0] = dataAdapter;
		setDataAdapters(dataAdapters);
	}

	/**
	 * Set the set of object graphs used by this engine.
	 */
	public void setDataAdapters(DataSource[] dataAdapters) {
		for (int i = 0; i < dataAdapters.length; i++) {
			DataSource dataAdapter = dataAdapters[i];
			TypeDomain typeDomain = dataAdapter.getTypeDomain();
			typeDomains.put(typeDomain.getTypeDomainId(), typeDomain);
		}
	}

}
