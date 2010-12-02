package com.berniecode.ogre;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.IOFailureException;
import com.berniecode.ogre.enginelib.NoSuchThingException;
import com.berniecode.ogre.enginelib.client.ClientEngine;
import com.berniecode.ogre.enginelib.server.ServerEngine;
import com.berniecode.ogre.server.pojods.PojoDataSource;

/**
 * Tests of the OGRE system in its most common configuration running from the server data source
 * through to the client data adapter
 * 
 * @author Bernie Sumption
 */
// TODO start using Spring to configure the tests.
public class EndToEndTest extends TestCase {

	private static final String TYPE_DOMAIN_ID = "com.berniecode.ogre.enginelib.EndToEndTests";

	private MockDownloadBridge dlBridge;

	@Override
	protected void setUp() throws Exception {
		// set up the server
		PojoDataSource ds = new PojoDataSource();
		ds.setClasses(EntityClassWithAllFields.class);
		ds.setTypeDomainId(TYPE_DOMAIN_ID);
		ds.initialise();

		ServerEngine se = new ServerEngine();
		se.setDataAdapter(ds);

		dlBridge = new MockDownloadBridge(se);
	};

	public void testInitialiseFailsWithBadTypeDomain() throws IOFailureException {

		boolean exceptionThrown = false;
		try {
			createClientEngine(dlBridge, "does not exist");
		} catch (NoSuchThingException e) {
			exceptionThrown = true;
		}
		assertTrue("The client engine should throw a NoSuchThingException if asked to initialise a non-existant type domain",
				exceptionThrown);

	}

	public void testCorrectTypeDomainProduced() throws IOFailureException {

		ClientEngine clientEngine = createClientEngine(dlBridge, TYPE_DOMAIN_ID);
		
		assertTrue("Subsequent calls to ClientEngine.getTypedomain() should return the same object, not a new TypeDomain fetched over the bridge",
				clientEngine.getTypeDomain() == clientEngine.getTypeDomain());
		
//		EDRTestUtils.assertTypeDomainsEqual(EntityClassWithAllFields.EXPECTED_ENTITY_TYPE, clientEngine.getTypeDomain());

	}

	private ClientEngine createClientEngine(MockDownloadBridge dlBridge, String typeDomain) throws IOFailureException {
		ClientEngine ce = new ClientEngine();
		ce.setTypeDomainId(typeDomain);
		ce.setDownloadAdapter(dlBridge);
		ce.initialise();
		return ce;
	}
}
