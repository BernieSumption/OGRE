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

package com.berniecode.ogre.demos.friendgraph.controller;

import com.berniecode.ogre.demos.friendgraph.model.MutableSocialNetwork;
import com.berniecode.ogre.demos.friendgraph.model.Person;
import com.berniecode.ogre.demos.friendgraph.model.PersonImpl;
import com.berniecode.ogre.demos.friendgraph.view.EditEventListener;
import com.berniecode.ogre.demos.friendgraph.view.FriendGraphView;
import com.berniecode.ogre.server.pojods.PojoDataSource;

public class ServerController implements EditEventListener {

	private final MutableSocialNetwork model;
	private FriendGraphView view;
	private final PojoDataSource dataSource;

	public ServerController(MutableSocialNetwork model, FriendGraphView view, PojoDataSource dataSource) {
		this.model = model;
		this.view = view;
		this.dataSource = dataSource;
		view.setEditEventListener(this);
		handleChange();
	}

	public void setPersonLocation(Person p, int x, int y) {
		PersonImpl pi = (PersonImpl) p;
		pi.setXPosition(x);
		pi.setYPosition(y);
		handleChange();
	}

	public void setPersonName(Person person, String name) {
		PersonImpl pi = (PersonImpl) person;
		pi.setName(name);
		handleChange();
	}

	public void setPersonPhotoJpeg(Person person, byte[] photoJpeg) {
		PersonImpl pi = (PersonImpl) person;
		pi.setPhotoJpeg(photoJpeg);
		handleChange();
	}

	public Person createNewPerson(String name, int x, int y) {
		Person p = new PersonImpl(model, name, null, x, y);
		model.addPerson(p);
		handleChange();
		return p;
	}

	public void deletePerson(Person person) {
		model.removePerson(person);
		handleChange();
	}

	public void addFriendship(Person person1, Person person2) {
		model.setPersonLikesPerson(person1, person2, true);
		handleChange();
	}

	public void removeFriendship(Person person1, Person person2) {
		model.setPersonLikesPerson(person1, person2, false);
		handleChange();
	}

	private void handleChange() {
		view.updateFromModel(model);
		dataSource.setEntityObjects(model.getPeople(), model.getFriendships());
	}

}
