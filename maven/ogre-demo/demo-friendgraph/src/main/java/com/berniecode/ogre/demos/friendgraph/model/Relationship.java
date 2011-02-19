package com.berniecode.ogre.demos.friendgraph.model;

/**
 * Expresses a one-way liking relationship
 *
 * @author Bernie Sumption
 */
public interface Relationship {

	/**
	 * @return the person who does the liking
	 */
	public Person getSubject();
	
	/**
	 * @return the person who is liked
	 */
	public Person getObject();

}
