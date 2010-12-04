package com.berniecode.ogre;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.client.ClientEngine;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.server.ServerEngine;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;
import com.berniecode.ogre.server.pojods.PojoDataSource;

/**
 * Tests of the OGRE system in its most common configuration running from the server data source
 * through to the client data adapter
 * 
 * @author Bernie Sumption
 */
public class EndToEndTest extends TestCase {

	private static final String TYPE_DOMAIN_ID = "com.berniecode.ogre.EndToEndTests";

	private static final String OBJECT_GRAPH_ID = "TestObjectGraph";

	private MockDownloadBridge dlBridge;

	@Override
	protected void setUp() throws Exception {
		// set up the server before each test
		PojoDataSource ds = new PojoDataSource();
		ds.setClasses(EntityClassWithAllFields.class);
		ds.setTypeDomainId(TYPE_DOMAIN_ID);
		ds.setObjectGraphId(OBJECT_GRAPH_ID);
		ds.initialise();
		
		ds.addEntityObjects(new EntityClassWithAllFields((byte)1, (byte)2, (short)3, (short)4, 5, 6, 7L, 8L));

		ServerEngine se = new ServerEngine();
		se.setDataAdapter(ds);
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
		assertTrue("Subsequent calls to ClientEngine.getTypedomain() should return the same object, not a new TypeDomain fetched over the bridge",
				clientEngine.getTypeDomain() == clientEngine.getTypeDomain());
	}

	public void testCorrectTypeDomainTransferred() throws Exception {

		ClientEngine clientEngine = createClientEngine(dlBridge, TYPE_DOMAIN_ID);
		
		assertTrue("Subsequent calls to ClientEngine.getTypedomain() should return the same object, not a new TypeDomain fetched over the bridge",
				clientEngine.getTypeDomain() == clientEngine.getTypeDomain());
		
		String actual = EDRDescriber.describeTypeDomain(clientEngine.getTypeDomain());
		String expected = 
				"TypeDomain com.berniecode.ogre.EndToEndTests" +
				"  EntityType com.berniecode.ogre.EntityClassWithAllFields" +
				"    8 bit integer property non_nullable_byte" +
				"    32 bit integer property non_nullable_int" +
				"    64 bit integer property non_nullable_long" +
				"    16 bit integer property non_nullable_short" +
				"    nullable 8 bit integer property nullable_byte" +
				"    nullable 32 bit integer property nullable_int" +
				"    nullable 64 bit integer property nullable_long" +
				"    nullable 16 bit integer property nullable_short";
		
		assertEqualsIgnoreWhitespace(expected, actual);

	}
	
	public void testCorrectObjectsTransferred() throws Exception {
		ClientEngine clientEngine = createClientEngine(dlBridge, TYPE_DOMAIN_ID);
		
		String actual = EDRDescriber.describeObjectGraph(clientEngine);
		String expected = 
			"ObjectGraph com.berniecode.ogre.EndToEndTests/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields" +
			"    non_nullable_byte: 1" +
			"    non_nullable_int: 5" +
			"    non_nullable_long: 7" +
			"    non_nullable_short: 3" +
			"    nullable_byte: 2" +
			"    nullable_int: 6" +
			"    nullable_long: 8" +
			"    nullable_short: 4";

		assertEqualsIgnoreWhitespace(expected, actual);
		
	}

	private void assertEqualsIgnoreWhitespace(String expected, String actual) {
		if (expected != null) {
			expected = expected.replaceAll("\\s+", "\n");
		}
		if (actual != null) {
			actual = actual.replaceAll("\\s+", "\n");
		}
		assertEquals(expected, actual);
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
