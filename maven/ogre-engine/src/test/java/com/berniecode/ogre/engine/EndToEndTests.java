package com.berniecode.ogre.engine;

import junit.framework.TestCase;

import com.berniecode.ogre.engine.client.ClientEngine;
import com.berniecode.ogre.engine.server.DataSource;
import com.berniecode.ogre.engine.server.PojoDataSource;
import com.berniecode.ogre.engine.server.ServerEngine;

/**
 * Tests of the OGRE system in its most common configuration running from the server data source
 * through to the client data adapter
 * 
 * @author Bernie Sumption
 */
public class EndToEndTests extends TestCase {

	private String TYPE_DOMAIN = "com.berniecode.ogre.engine.EndToEndTests";

	public void testEndToEnd() {

		DataSource ds = new PojoDataSource();

		ServerEngine se = new ServerEngine();

		se.setDataAdapter(ds);

		MockDownloadBridge dlBridge = new MockDownloadBridge(se);

		ClientEngine ce = new ClientEngine();
		ce.setTypeDomainId(TYPE_DOMAIN);
		ce.setDownloadAdapter(dlBridge);

	}
}
