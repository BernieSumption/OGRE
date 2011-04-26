package com.berniecode.ogre.demos.friendgraph.model;

/**
 * Expresses a one-way liking relationship
 *
 * @author Bernie Sumption
 */
public interface Friendship {

	/**
	 * @return the person who does the liking
	 */
	public Person getLiker();
	
	/**
	 * @return the person who is liked
	 */
	public Person getLikee();

}
