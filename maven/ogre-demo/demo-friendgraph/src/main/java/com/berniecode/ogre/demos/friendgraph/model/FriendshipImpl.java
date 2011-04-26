package com.berniecode.ogre.demos.friendgraph.model;

public class FriendshipImpl implements Friendship {

	private final Person subject;
	private final Person object;

	public FriendshipImpl(Person subject, Person object) {
		this.subject = subject;
		this.object = object;
	}

	public Person getLiker() {
		return subject;
	}

	public Person getLikee() {
		return object;
	}
	
	@Override
	public String toString() {
		return subject + " to " + object;
	}

}
