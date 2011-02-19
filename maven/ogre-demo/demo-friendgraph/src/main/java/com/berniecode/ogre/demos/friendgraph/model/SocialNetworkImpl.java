package com.berniecode.ogre.demos.friendgraph.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple implementation of {@link SocialNetwork}
 *
 * @author Bernie Sumption
 */
public class SocialNetworkImpl implements SocialNetwork {

	private List<Person> people = new ArrayList<Person>();
	private List<Relationship> likesRelationships = new ArrayList<Relationship>();

	public Collection<Person> getPeople() {
		return people;
	}

	public Collection<Relationship> getLikesRelationships() {
		return likesRelationships;
	}

	public void addPerson(Person person) {
		people.add(person);
	}

	public void removePerson(Person person) {
		people.remove(person);
	}
	
	public boolean getPersonLikesPerson(Person subject, Person object) {
		return findRelationship(subject, object) != null;
	}
	
	public void setPersonLikesPerson(Person subject, Person object, boolean likes) {
		Relationship existing = findRelationship(subject, object);
		if (existing != null && !likes) {
			likesRelationships.remove(existing);
		} else if (existing == null && likes) {
			likesRelationships.add(new RelationshipImpl(subject, object));
		}
	}
	
	private Relationship findRelationship(Person subject, Person object) {
		for (Relationship relationship: likesRelationships) {
			if (relationship.getObject().equals(subject) && relationship.getSubject().equals(object)) {
				return relationship;
			}
		}
		return null;
	}

}
