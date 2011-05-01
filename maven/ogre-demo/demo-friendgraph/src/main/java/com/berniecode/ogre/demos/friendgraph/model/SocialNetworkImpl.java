/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

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
		for (Friendship friendship : friendships) {
			if (friendship.getLiker().equals(subject) && friendship.getLikee().equals(object)) {
				return friendship;
			}
		}
		return null;
	}

}
