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
	 * @return all the {@link Friendship}s in this network
	 */
	public Collection<Friendship> getFriendships();

	/**
	 * @return whether one person likes another
	 */
	public boolean getPersonLikesPerson(Person subject, Person object);

}
