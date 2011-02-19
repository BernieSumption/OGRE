package com.berniecode.ogre.demos.friendgraph.view;

import com.berniecode.ogre.demos.friendgraph.model.Person;

/**
 * Handles user editing operations in the view. In order for the view to update as a result of a
 * user editing operation, the edit event listener must itself update the model.
 * 
 * @author Bernie Sumption
 */
public interface EditEventListener {
	
	void setPersonLocation(Person p, int x, int y);
	
	void setPersonName(Person p, String name);

	Person createNewPerson(String name, int x, int y);

	void deletePerson(Person person);

	void setPersonPhotoJpeg(Person person, byte[] result);

	void addFriendship(Person person1, Person person2);

	void removeFriendship(Person person1, Person person2);

}
