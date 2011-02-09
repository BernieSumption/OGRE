package com.berniecode.ogre.demos.friendgraph;

import com.berniecode.ogre.demos.friendgraph.controller.EditController;
import com.berniecode.ogre.demos.friendgraph.model.FriendGraphModel;
import com.berniecode.ogre.demos.friendgraph.model.PersonImpl;
import com.berniecode.ogre.demos.friendgraph.view.FriendGraphView;

public class Main {

	public static void main(String[] args) {
		FriendGraphModel model = new FriendGraphModel();
		model.addPerson(new PersonImpl("Bernie", null, 50, 80));
		
		FriendGraphView view = new FriendGraphView(true);
		view.updateFromModel(model);
		
		new EditController(model, view);
		
		view.setVisible(true);
	}
}
