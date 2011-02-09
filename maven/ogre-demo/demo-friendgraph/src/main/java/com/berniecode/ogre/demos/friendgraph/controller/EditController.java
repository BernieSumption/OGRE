package com.berniecode.ogre.demos.friendgraph.controller;

import com.berniecode.ogre.demos.friendgraph.model.FriendGraphModel;
import com.berniecode.ogre.demos.friendgraph.model.Person;
import com.berniecode.ogre.demos.friendgraph.model.PersonImpl;
import com.berniecode.ogre.demos.friendgraph.view.EditEventListener;
import com.berniecode.ogre.demos.friendgraph.view.FriendGraphView;

public class EditController implements EditEventListener {

	private final FriendGraphModel model;
	private FriendGraphView view;

	public EditController(FriendGraphModel model, FriendGraphView view) {
		this.model = model;
		this.view = view;
		view.setEditEventListener(this);
	}

	public void setPersonLocation(Person p, int x, int y) {
		PersonImpl pi = (PersonImpl) p;
		pi.setXPosition(x);
		pi.setYPosition(y);
		view.updateFromModel(model);
	}

	public void setPersonName(Person person, String name) {
		PersonImpl pi = (PersonImpl) person;
		pi.setName(name);
		view.updateFromModel(model);
	}

	public void setPersonPhotoJpeg(Person person, byte[] photoJpeg) {
		PersonImpl pi = (PersonImpl) person;
		pi.setPhotoJpeg(photoJpeg);
		view.updateFromModel(model);
	}

	public Person createNewPerson(String name, int x, int y) {
		Person p = new PersonImpl(name, null, x, y);
		model.addPerson(p);
		view.updateFromModel(model);
		return p;
	}

	public void deletePerson(Person person) {
		model.removePerson(person);
		view.updateFromModel(model);
	}

}
