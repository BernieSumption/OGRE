package com.berniecode.ogre.enginelib.shared;

/**
 * A simple map interface, used in preference to java.util.Map because it is smaller, and easier to
 * implement in other languages.
 * 
 * @author Bernie Sumption
 */
//TODO: change this to EntityMap: long to Entity
public interface SimpleMap {

	/**
	 * Add a key/value pair to the map
	 */
	void put(Object key, Object value);

	/**
	 * Get a value from the map, or null if the key does not exist
	 */
	Object get(Object key);

	/**
	 * Check whether this map contains a certain key
	 */
	boolean contains(Object key);
	
	/**
	 * @return all the values of this map
	 */
	Object[] getValues();

}
