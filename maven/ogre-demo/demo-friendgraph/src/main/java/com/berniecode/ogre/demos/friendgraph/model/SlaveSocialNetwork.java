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

	public Collection<Relationship> getLikesRelationships() {
		return facade.getEntitiesByType(Relationship.class);
	}

	public boolean getPersonLikesPerson(Person subject, Person object) {
		for (Relationship r: getLikesRelationships()) {
			if (subject.equals(r.getSubject()) && object.equals(r.getObject())) {
				return true;
			}
		}
		return false;
	}

}
