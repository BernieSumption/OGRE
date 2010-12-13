package com.berniecode.ogre;


import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.client.ClientEngine;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
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

	private InProcessDownloadBridge dlBridge;
	private MockMessageBridge msgBridge;
	private PojoDataSource dataSource;
	private ServerEngine serverEngine;
	private EntityClassWithAllFields initialEntityObject;

	@Override
	protected void setUp() throws Exception {
		OgreLog.info("EndToEndTest.setUp() Creating new OGRE server");
		dataSource = new PojoDataSource();
		dataSource.setEDRMapper(new DefaultEDRMapper(TYPE_DOMAIN_ID, EntityClassWithAllFields.class));
		dataSource.setObjectGraphId(OBJECT_GRAPH_ID);
		dataSource.initialise();

		serverEngine = new ServerEngine();
		serverEngine.setDataSource(dataSource);
		serverEngine.setMessageAdapter(msgBridge = new MockMessageBridge());
		serverEngine.initialise();

		dlBridge = new InProcessDownloadBridge(serverEngine);
		
		dataSource.setEntityObjects(initialEntityObject = new EntityClassWithAllFields((byte)1, (byte)2, (short)3, (short)4, 5, 6, 7L, 8L, "Shizzle", 9.0F, 10.0F, 11.0, 12.0));
	};

	public void testFetchTypeDomain() throws Exception {

		boolean exceptionThrown = false;
		try {
			createClientEngine("does not exist");
		} catch (NoSuchThingException e) {
			exceptionThrown = true;
		}
		assertTrue("The client engine should throw a NoSuchThingException if asked to initialise a non-existant type domain",
				exceptionThrown);

		
		ClientEngine clientEngine = createClientEngine();
		assertTrue("Subsequent calls to ClientEngineTest.getTypedomain() should return the same object, not a new TypeDomain fetched over the bridge",
				clientEngine.getTypeDomain() == clientEngine.getTypeDomain());
	}

	public void testCorrectTypeDomainTransferred() throws Exception {

		ClientEngine clientEngine = createClientEngine();
		
		assertTrue("Subsequent calls to ClientEngineTest.getTypedomain() should return the same object, not a new TypeDomain fetched over the bridge",
				clientEngine.getTypeDomain() == clientEngine.getTypeDomain());
		
		assertTypeDomainState(
				"TypeDomain com.berniecode.ogre.test.TypeDomain" +
				"  0. EntityType com.berniecode.ogre.EntityClassWithAllFields" +
				"       8 bit integer property non_nullable_byte" +
				"       64 bit float property non_nullable_double" +
				"       32 bit float property non_nullable_float" +
				"       32 bit integer property non_nullable_int" +
				"       64 bit integer property non_nullable_long" +
				"       16 bit integer property non_nullable_short" +
				"       nullable 8 bit integer property nullable_byte" +
				"       nullable 64 bit float property nullable_double" +
				"       nullable 32 bit float property nullable_float" +
				"       nullable 32 bit integer property nullable_int" +
				"       nullable 64 bit integer property nullable_long" +
				"       nullable 16 bit integer property nullable_short" +
				"       string property string",
				clientEngine.getTypeDomain());

	}
	
	public void testCorrectObjectsTransferred() throws Exception {
		ClientEngine clientEngine = createClientEngine();
		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#1" +
			"    non_nullable_byte=1" +
			"    non_nullable_double=11.0" +
			"    non_nullable_float=9.0" +
			"    non_nullable_int=5" +
			"    non_nullable_long=7" +
			"    non_nullable_short=3" +
			"    nullable_byte=2" +
			"    nullable_double=12.0" +
			"    nullable_float=10.0" +
			"    nullable_int=6" +
			"    nullable_long=8" +
			"    nullable_short=4" +
			"    string=Shizzle",
			clientEngine);
		
	}
	
	public void testChangesPropagated() throws Exception {
		ClientEngine clientEngine = createClientEngine();
		
		initialEntityObject.setNullableInt(null);
		initialEntityObject.setNonNullableLong(42L);
		initialEntityObject.setString("Fizzle");
		
		assertEquals(1, msgBridge.getMessageCount());

		// changes propagated
		dataSource.setEntityObjects(initialEntityObject);

		
		assertEquals(2, msgBridge.getMessageCount());
		assertUpdateMessageState(
				"UpdateMessage for object graph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
				"  partial values:" +
				"    EntityUpdate for com.berniecode.ogre.EntityClassWithAllFields#1" +
				"      non_nullable_long=42" +
				"      nullable_int=null" +
				"      string=Fizzle",
				msgBridge.getLastUpdateMessage(), dataSource.getTypeDomain());

		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#1" +
			"    non_nullable_byte=1" +
			"    non_nullable_double=11.0" +
			"    non_nullable_float=9.0" +
			"    non_nullable_int=5" +
			"    non_nullable_long=42" +
			"    non_nullable_short=3" +
			"    nullable_byte=2" +
			"    nullable_double=12.0" +
			"    nullable_float=10.0" +
			"    nullable_int=null" +
			"    nullable_long=8" +
			"    nullable_short=4" +
			"    string=Fizzle",
			clientEngine);
		
		// new objects propagated
		EntityClassWithAllFields newEntityObject = new EntityClassWithAllFields((byte)11, (byte)12, (short)13, (short)14, 15, 16, 17L, 18L, "my bizzle", 19.0F, 20.0F, 21.0, 22.0);

		dataSource.setEntityObjects(initialEntityObject, newEntityObject);

		assertEquals(3, msgBridge.getMessageCount());
		assertUpdateMessageState(
				"UpdateMessage for object graph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    EntityUpdate for com.berniecode.ogre.EntityClassWithAllFields#2" +
				"      non_nullable_byte=11" +
				"      non_nullable_double=21.0" +
				"      non_nullable_float=19.0" +
				"      non_nullable_int=15" +
				"      non_nullable_long=17" +
				"      non_nullable_short=13" +
				"      nullable_byte=12" +
				"      nullable_double=22.0" +
				"      nullable_float=20.0" +
				"      nullable_int=16" +
				"      nullable_long=18" +
				"      nullable_short=14" +
				"      string=my bizzle",
				msgBridge.getLastUpdateMessage(), dataSource.getTypeDomain());
		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#1" +
			"    non_nullable_byte=1" +
			"    non_nullable_double=11.0" +
			"    non_nullable_float=9.0" +
			"    non_nullable_int=5" +
			"    non_nullable_long=42" +
			"    non_nullable_short=3" +
			"    nullable_byte=2" +
			"    nullable_double=12.0" +
			"    nullable_float=10.0" +
			"    nullable_int=null" +
			"    nullable_long=8" +
			"    nullable_short=4" +
			"    string=Fizzle" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#2" +
			"    non_nullable_byte=11" +
			"    non_nullable_double=21.0" +
			"    non_nullable_float=19.0" +
			"    non_nullable_int=15" +
			"    non_nullable_long=17" +
			"    non_nullable_short=13" +
			"    nullable_byte=12" +
			"    nullable_double=22.0" +
			"    nullable_float=20.0" +
			"    nullable_int=16" +
			"    nullable_long=18" +
			"    nullable_short=14" +
			"    string=my bizzle",
			clientEngine);
		
		// removes propagated
		dataSource.setEntityObjects(newEntityObject);

		assertEquals(4, msgBridge.getMessageCount());
		assertUpdateMessageState(
				"UpdateMessage for object graph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
				"  deleted entities:" +
				"    EntityDelete for com.berniecode.ogre.EntityClassWithAllFields#1",
				msgBridge.getLastUpdateMessage(), dataSource.getTypeDomain());
		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#2" +
			"    non_nullable_byte=11" +
			"    non_nullable_double=21.0" +
			"    non_nullable_float=19.0" +
			"    non_nullable_int=15" +
			"    non_nullable_long=17" +
			"    non_nullable_short=13" +
			"    nullable_byte=12" +
			"    nullable_double=22.0" +
			"    nullable_float=20.0" +
			"    nullable_int=16" +
			"    nullable_long=18" +
			"    nullable_short=14" +
			"    string=my bizzle",
			clientEngine);
		
		// non-changes don't create extra update messages
		dataSource.setEntityObjects(newEntityObject);
		assertEquals(4, msgBridge.getMessageCount());
		
		// removes propagated
		dataSource.setEntityObjects();

		assertEquals(5, msgBridge.getMessageCount());
		assertUpdateMessageState(
				"UpdateMessage for object graph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
				"  deleted entities:" +
				"    EntityDelete for com.berniecode.ogre.EntityClassWithAllFields#2",
				msgBridge.getLastUpdateMessage(), dataSource.getTypeDomain());
		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph",
			clientEngine);
		
		//TODO for each property, set it to the same value but different object and test no message transferred
	}
	private ClientEngine createClientEngine() throws Exception {
		return createClientEngine(TYPE_DOMAIN_ID);
	}

	private ClientEngine createClientEngine(String typeDomainId) throws Exception {
		ClientEngine ce = new ClientEngine();
		ce.setTypeDomainId(typeDomainId);
		ce.setDownloadAdapter(dlBridge);
		ce.setMessageAdapter(msgBridge);
		ce.setObjectGraphId(OBJECT_GRAPH_ID);
		ce.initialise();
		return ce;
	}
}
