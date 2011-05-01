package com.berniecode.ogre.demos.friendgraph;

import java.net.InetAddress;

import com.berniecode.ogre.client.ClientFacade;
import com.berniecode.ogre.demos.friendgraph.model.SlaveSocialNetwork;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetwork;
import com.berniecode.ogre.demos.friendgraph.view.FriendGraphView;
import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.tcpbridge.SimpleTcpTransportClient;

public class ClientMain {

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.err.println("Requires 2 arguments: [host] and [port]");
			System.exit(1);
		}

		InetAddress host = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);

		final FriendGraphView view = new FriendGraphView(false);

		ClientEngine ce = new ClientEngine();
		ce.setTypeDomainId("friendgraph");
		ce.setObjectGraphId("demo");
		SimpleTcpTransportClient client = new SimpleTcpTransportClient(host, port, null);
		ce.setTransportAdapter(client);
		ce.initialise();

		ClientFacade facade = new ClientFacade(ce);

		final SocialNetwork model = new SlaveSocialNetwork(facade);
		ce.setGraphUpdateListener(new GraphUpdateListener() {
			public void acceptGraphUpdate(GraphUpdate update) {
				view.updateFromModel(model);
			}
		});
		view.updateFromModel(model);

		view.setVisible(true);
	}
}
