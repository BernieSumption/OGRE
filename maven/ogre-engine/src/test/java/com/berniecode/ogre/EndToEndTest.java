package com.berniecode.ogre;


import com.berniecode.ogre.enginelib.client.ClientEngine;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreLog;
import com.berniecode.ogre.enginelib.server.ServerEngine;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;
import com.berniecode.ogre.server.pojods.PojoDataSource;

/**
 * Tests of the OGRE system in its most common configuration running from the server data source
 * through to the client data adapter
 * 
 * @author Bernie Sumption
 */
public class EndToEndTest extends OgreTestCase {

	private MockDownloadBridge dlBridge;

	@Override
	protected void setUp() throws Exception {
		OgreLog.info("EndToEndTest.setUp() Creating new OGRE server");
		// set up the server before each test
		PojoDataSource ds = new PojoDataSource();
		ds.setEDRMapper(new DefaultEDRMapper(TYPE_DOMAIN_ID, EntityClassWithAllFields.class));
		ds.setObjectGraphId(OBJECT_GRAPH_ID);
		ds.initialise();
		
		ds.addEntityObjects(new EntityClassWithAllFields((byte)1, (byte)2, (short)3, (short)4, 5, 6, 7L, 8L));

		ServerEngine se = new ServerEngine();
		se.setDataSource(ds);
		se.initialise();

		dlBridge = new MockDownloadBridge(se);
	};

	public void testFetchTypeDomain() throws Exception {

		boolean exceptionThrown = false;
		try {
			createClientEngine(dlBridge, "does not exist");
		} catch (NoSuchThingException e) {
			exceptionThrown = true;
		}
		assertTrue("The client engine should throw a NoSuchThingException if asked to initialise a non-existant type domain",
				exceptionThrown);

		
		ClientEngine clientEngine = createClientEngine(dlBridge, TYPE_DOMAIN_ID);
		assertTrue("Subsequent calls to ClientEngineTest.getTypedomain() should return the same object, not a new TypeDomain fetched over the bridge",
				clientEngine.getTypeDomain() == clientEngine.getTypeDomain());
	}

	public void testCorrectTypeDomainTransferred() throws Exception {

		ClientEngine clientEngine = createClientEngine(dlBridge, TYPE_DOMAIN_ID);
		
		assertTrue("Subsequent calls to ClientEngineTest.getTypedomain() should return the same object, not a new TypeDomain fetched over the bridge",
				clientEngine.getTypeDomain() == clientEngine.getTypeDomain());
		
		assertEquals(
				"TypeDomain com.berniecode.ogre.test.TypeDomain" +
				"  EntityType com.berniecode.ogre.EntityClassWithAllFields" +
				"    8 bit integer property non_nullable_byte" +
				"    32 bit integer property non_nullable_int" +
				"    64 bit integer property non_nullable_long" +
				"    16 bit integer property non_nullable_short" +
				"    nullable 8 bit integer property nullable_byte" +
				"    nullable 32 bit integer property nullable_int" +
				"    nullable 64 bit integer property nullable_long" +
				"    nullable 16 bit integer property nullable_short",
				clientEngine.getTypeDomain());

	}
	
	public void testCorrectObjectsTransferred() throws Exception {
		ClientEngine clientEngine = createClientEngine(dlBridge, TYPE_DOMAIN_ID);
		 
		assertEquals(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#1" +
			"    non_nullable_byte: 1" +
			"    non_nullable_int: 5" +
			"    non_nullable_long: 7" +
			"    non_nullable_short: 3" +
			"    nullable_byte: 2" +
			"    nullable_int: 6" +
			"    nullable_long: 8" +
			"    nullable_short: 4",
			clientEngine);
		
	}

	private ClientEngine createClientEngine(MockDownloadBridge dlBridge, String typeDomain) throws Exception {
		ClientEngine ce = new ClientEngine();
		ce.setTypeDomainId(typeDomain);
		ce.setDownloadAdapter(dlBridge);
		ce.setObjectGraphId(OBJECT_GRAPH_ID);
		ce.initialise();
		return ce;
	}
}
