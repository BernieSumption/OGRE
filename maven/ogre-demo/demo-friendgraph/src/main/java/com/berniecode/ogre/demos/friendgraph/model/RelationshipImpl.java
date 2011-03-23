package com.berniecode.ogre.demos.friendgraph.model;

public class RelationshipImpl implements Relationship {

	private final Person subject;
	private final Person object;

	public RelationshipImpl(Person subject, Person object) {
		this.subject = subject;
		this.object = object;
	}

	public Person getSubject() {
		return subject;
	}

	public Person getObject() {
		return object;
	}
	
	@Override
	public String toString() {
		return subject + " to " + object;
	}

}
