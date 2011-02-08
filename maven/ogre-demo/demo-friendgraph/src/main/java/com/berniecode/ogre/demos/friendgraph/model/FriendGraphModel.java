package com.berniecode.ogre.demos.friendgraph.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A collection of friends, with relationships
 *
 * @author Bernie Sumption
 */
public class FriendGraphModel {
	
	private List<Person> people = new ArrayList<Person>();

	public Collection<Person> getPeople() {
		return people;
	}

	public void addPerson(Person person) {
		people.add(person);
	}

	public void removePerson(Person person) {
		people.remove(person);
	}

}
