package com.berniecode.ogre.demos.friendgraph;

import javax.swing.UIManager;

import com.berniecode.ogre.demos.friendgraph.controller.EditController;
import com.berniecode.ogre.demos.friendgraph.model.Person;
import com.berniecode.ogre.demos.friendgraph.model.PersonImpl;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetwork;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetworkImpl;
import com.berniecode.ogre.demos.friendgraph.view.FriendGraphView;

public class Main {

	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Usage:");
			System.err.println("    java -jar demos-friendgraph.jar server port");
			System.err.println("    java -jar demos-friendgraph.jar client ip-address port");
		}
		
		SocialNetwork model = new SocialNetworkImpl();
		Person bernie;
		model.addPerson(bernie = new PersonImpl(model, "Bernie", null, 50, 80));
		Person jude;
		model.addPerson(jude = new PersonImpl(model, "Jude", null, 250, 30));
		model.setPersonLikesPerson(bernie, jude, true);
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Can't set look and feel: " + e.getMessage());
		}
		
		FriendGraphView view = new FriendGraphView(true);
		view.updateFromModel(model);
		
		new EditController(model, view);
		
		view.setVisible(true);
	}
}
