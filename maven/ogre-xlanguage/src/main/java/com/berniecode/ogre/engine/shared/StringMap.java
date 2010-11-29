package com.berniecode.ogre.engine.shared;

/**
 * A simple map interface, whose keys are always strings used in preference to
 * {@link java.util.List} because it is smaller, and therefore easier to re-implement in other
 * languages (and because not all languages have support for non-string hashtable keys)
 * 
 * @author Bernie Sumption
 */
public interface StringMap<T> {

	/**
	 * Add a key/value pair to the map
	 */
	void put(String key, T value);

	/**
	 * Get a value from the map, or null if the key does not exist
	 */
	T get(String key);

	/**
	 * Check whether this map contains a certain key
	 */
	boolean contains(String typeDomainId);

}
