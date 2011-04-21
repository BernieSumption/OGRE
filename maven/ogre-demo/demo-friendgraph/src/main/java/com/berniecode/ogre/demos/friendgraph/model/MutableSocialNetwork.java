package com.berniecode.ogre.demos.friendgraph.model;



/**
 * A {@link SocialNetwork} with methods to add people and relationships.
 *
 * @author Bernie Sumption
 */
public interface MutableSocialNetwork extends SocialNetwork {

	/**
	 * Add a person to the network
	 */
	public void addPerson(Person person);

	/**
	 * Remove a person, and all their relationships, from the network
	 */
	public void removePerson(Person person);
	
	/**
	 * Set whether one person likes another
	 */
	public void setPersonLikesPerson(Person subject, Person object, boolean likes);
}
