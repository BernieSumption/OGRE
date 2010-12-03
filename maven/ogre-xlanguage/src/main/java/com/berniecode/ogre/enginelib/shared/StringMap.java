package com.berniecode.ogre.enginelib.shared;

/**
 * A simple map interface, whose keys are always strings. This is used instead of Java's
 * {@link java.util.Map} because it is smaller, and therefore easier to re-implement in other
 * languages (and because not all languages have support for non-string hashtable keys)
 * 
 * @author Bernie Sumption
 */
public interface StringMap {

	/**
	 * Add a key/value pair to the map
	 */
	void put(String key, Object value);

	/**
	 * Get a value from the map, or null if the key does not exist
	 */
	Object get(String key);

	/**
	 * Check whether this map contains a certain key
	 */
	boolean contains(String key);

}