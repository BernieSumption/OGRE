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
