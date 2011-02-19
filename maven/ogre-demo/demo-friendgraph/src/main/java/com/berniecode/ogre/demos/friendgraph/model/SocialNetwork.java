package com.berniecode.ogre.demos.friendgraph.model;

import java.util.Collection;


/**
 * A collection of friends, with relationships
 *
 * @author Bernie Sumption
 */
public interface SocialNetwork {

	/**
	 * @return all the {@link Person}s in this network
	 */
	public Collection<Person> getPeople();

	/**
	 * @return all the {@link Relationship}s in this network
	 */
	public Collection<Relationship> getLikesRelationships();

	/**
	 * Add a person to the network
	 */
	public void addPerson(Person person);

	/**
	 * Remove a person, and all their relationships, from the network
	 */
	public void removePerson(Person person);

	/**
	 * @return whether one person likes another
	 */
	public boolean getPersonLikesPerson(Person subject, Person object);
	
	/**
	 * Set whether one person likes another
	 */
	public void setPersonLikesPerson(Person subject, Person object, boolean likes);
}
