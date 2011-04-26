package com.berniecode.ogre.demos.friendgraph.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A simple implementation of {@link MutableSocialNetwork}
 *
 * @author Bernie Sumption
 */
public class SocialNetworkImpl implements MutableSocialNetwork {

	private List<Person> people = new ArrayList<Person>();
	private List<Friendship> friendships = new ArrayList<Friendship>();

	public Collection<Person> getPeople() {
		return people;
	}

	public Collection<Friendship> getFriendships() {
		return friendships;
	}

	public void addPerson(Person person) {
		people.add(person);
	}

	public void removePerson(Person person) {
		people.remove(person);
		Iterator<Friendship> it = friendships.iterator();
		while (it.hasNext()) {
			Friendship friendship = it.next();
			if (friendship.getLiker().equals(person) || friendship.getLikee().equals(person)) {
				it.remove();
			}
		}
	}
	
	public boolean getPersonLikesPerson(Person subject, Person object) {
		return findFriendship(subject, object) != null;
	}
	
	public void setPersonLikesPerson(Person subject, Person object, boolean likes) {
		Friendship existing = findFriendship(subject, object);
		if (existing != null && !likes) {
			friendships.remove(existing);
		} else if (existing == null && likes) {
			friendships.add(new FriendshipImpl(subject, object));
		}
	}
	
	private Friendship findFriendship(Person subject, Person object) {
		for (Friendship friendship: friendships) {
			if (friendship.getLiker().equals(subject) && friendship.getLikee().equals(object)) {
				return friendship;
			}
		}
		return null;
	}

}
