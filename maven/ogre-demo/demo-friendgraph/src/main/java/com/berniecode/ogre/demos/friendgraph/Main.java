package com.berniecode.ogre.demos.friendgraph;

import com.berniecode.ogre.demos.friendgraph.controller.EditController;
import com.berniecode.ogre.demos.friendgraph.model.PersonImpl;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetwork;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetworkImpl;
import com.berniecode.ogre.demos.friendgraph.view.FriendGraphView;

public class Main {

	public static void main(String[] args) {
		SocialNetwork model = new SocialNetworkImpl();
		model.addPerson(new PersonImpl(model, "Bernie", null, 50, 80));
		
		FriendGraphView view = new FriendGraphView(true);
		view.updateFromModel(model);
		
		new EditController(model, view);
		
		view.setVisible(true);
	}
}
