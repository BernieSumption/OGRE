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

import com.berniecode.ogre.demos.friendgraph.controller.ServerController;
import com.berniecode.ogre.demos.friendgraph.model.Friendship;
import com.berniecode.ogre.demos.friendgraph.model.MutableSocialNetwork;
import com.berniecode.ogre.demos.friendgraph.model.Person;
import com.berniecode.ogre.demos.friendgraph.model.PersonImpl;
import com.berniecode.ogre.demos.friendgraph.model.SocialNetworkImpl;
import com.berniecode.ogre.demos.friendgraph.view.FriendGraphView;
import com.berniecode.ogre.server.SerialisedDataSource;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;
import com.berniecode.ogre.server.pojods.PojoDataSource;
import com.berniecode.ogre.tcpbridge.SimpleTcpTransportServer;

public class ServerMain {

	private static SimpleTcpTransportServer server;

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.err.println("Requires 2 arguments: [host] and [port]");
			System.exit(1);
		}

		InetAddress host = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);

		final FriendGraphView view = new FriendGraphView(true);

		MutableSocialNetwork model = new SocialNetworkImpl();
		populateSampleData((MutableSocialNetwork) model);

		PojoDataSource ds = new PojoDataSource();
		ds.setEDRMapper(new DefaultEDRMapper("friendgraph", Person.class, Friendship.class));
		ds.setObjectGraphId("demo");
		ds.initialise();
		server = new SimpleTcpTransportServer(host, port, new SerialisedDataSource(ds, null));

		new ServerController((MutableSocialNetwork) model, view, ds);
		view.updateFromModel(model);

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
