package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A simple list interface used instead of java.util.List because it is smaller and easier to
 * implement in other languges
 * 
 * @author Bernie Sumption
 */
public interface SimpleList {
	
	public void add(Object object);
	
	public void addAll(Object[] object);
	
	public int size();
	
	/**
	 * Copy the elements in this list into the specified array.
	 * 
	 * @throws OgreException if the destination array is not the same length as this list
	 */
	void copyToArray(Object[] destination);

}
