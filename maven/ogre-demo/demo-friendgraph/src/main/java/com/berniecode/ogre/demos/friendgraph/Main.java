package com.berniecode.ogre.demos.friendgraph;

import java.net.InetAddress;

import javax.swing.UIManager;

import com.berniecode.ogre.client.ClientFacade;
import com.berniecode.ogre.demos.friendgraph.controller.ServerController;
import com.berniecode.ogre.demos.friendgraph.model.MutableSocialNetwork;
import com.berniecode.ogre.demos.friendgraph.model.Person;
import com.berniecode.ogre.demos.friendgraph.model.PersonImpl;
import com.berniecode.ogre.demos.friendgraph.model.Relationship;
import com.berniecode.ogre.demos.friendgraph.model.SlaveSocialNetwork;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetwork;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetworkImpl;
import com.berniecode.ogre.demos.friendgraph.view.FriendGraphView;
import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.server.SerialisedDataSource;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;
import com.berniecode.ogre.server.pojods.PojoDataSource;
import com.berniecode.ogre.tcpbridge.TcpBridgeClient;
import com.berniecode.ogre.tcpbridge.TcpBridgeServer;

public class Main {

	private static TcpBridgeServer server;

	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			System.err.println("Requires 3 arguments: [\"server\"|\"client\"] [host] [port]");
			System.err.println("    e.g. server localhost 8080");
			System.err.println("    or   client 192.168.0.5 8080");
			System.exit(1);
		}
		
		boolean serverMode = args[0].equals("server");
		InetAddress host = InetAddress.getByName(args[1]);
		int port = Integer.parseInt(args[2]);
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Can't set look and feel: " + e.getMessage());
		}

		final FriendGraphView view = new FriendGraphView(serverMode);

		if (serverMode) {
			MutableSocialNetwork model = new SocialNetworkImpl();
			populateSampleData((MutableSocialNetwork)model);
			
			PojoDataSource ds = new PojoDataSource();
			ds.setEDRMapper(new DefaultEDRMapper("friendgraph", Person.class, Relationship.class));
			ds.setObjectGraphId("demo");
			ds.initialise();
			
			server = new TcpBridgeServer();
			server.setDataSource(new SerialisedDataSource(ds, null));
			server.setHost(host);
			server.setPort(port);
			server.initialise();
			
			new ServerController((MutableSocialNetwork) model, view, ds);

			view.updateFromModel(model);
		} else {

			ClientEngine ce = new ClientEngine();
			ce.setTypeDomainId("friendgraph");
			ce.setObjectGraphId("demo");
			TcpBridgeClient client = new TcpBridgeClient(host, port);
			ce.setDownloadAdapter(client);
			ce.setMessageAdapter(client);
			ce.initialise();
			
			ClientFacade facade = new ClientFacade(ce);
			
			final SocialNetwork model = new SlaveSocialNetwork(facade);
			ce.setGraphUpdateListener(new GraphUpdateListener() {
				public void acceptGraphUpdate(GraphUpdate update) {
					view.updateFromModel(model);
				}
			});
		}
		
		view.setVisible(true);
	}

	private static void populateSampleData(MutableSocialNetwork model) {
		Person bernie;
		model.addPerson(bernie = new PersonImpl(model, "Bernie", null, 50, 80));
		Person jude;
		model.addPerson(jude = new PersonImpl(model, "Jude", null, 250, 30));
		model.setPersonLikesPerson(bernie, jude, true);
	}
}

