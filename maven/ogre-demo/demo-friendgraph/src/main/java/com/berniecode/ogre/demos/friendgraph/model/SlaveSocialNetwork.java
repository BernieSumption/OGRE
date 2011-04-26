package com.berniecode.ogre.demos.friendgraph.model;

import java.util.Collection;

import com.berniecode.ogre.client.ClientFacade;

public class SlaveSocialNetwork implements SocialNetwork {

	private final ClientFacade facade;

	public SlaveSocialNetwork(ClientFacade facade) {
		this.facade = facade;
	}

	public Collection<Person> getPeople() {
		return facade.getEntitiesByType(Person.class);
	}

	public Collection<Friendship> getFriendships() {
		return facade.getEntitiesByType(Friendship.class);
	}

	public boolean getPersonLikesPerson(Person subject, Person object) {
		for (Friendship r: getFriendships()) {
			if (subject.equals(r.getLiker()) && object.equals(r.getLikee())) {
				return true;
			}
		}
		return false;
	}

}
