package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.client.ClientEngine;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.server.ServerEngine;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;
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
		dataSource.setEDRMapper(new DefaultEDRMapper(TYPE_DOMAIN_ID, EntityClassWithAllFields.class, EntityElement.class));
		dataSource.setObjectGraphId(OBJECT_GRAPH_ID);
		dataSource.initialise();

		serverEngine = new ServerEngine();
		serverEngine.setDataSource(dataSource);
		serverEngine.setMessageAdapter(msgBridge = new MockMessageBridge());
		serverEngine.initialise();

		dlBridge = new InProcessDownloadBridge(serverEngine);
		
		dataSource.setEntityObjects(initialEntityObject = new EntityClassWithAllFields((byte)1, (byte)2, (short)3, (short)4, 5, 6, 7L, 8L, "Shizzle", 9.0F, 10.0F, 11.0, 12.0, byteArray(1, 2, 3), new EntityElement("Hi!")));
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
				"       byte-array property bytes" +
				"       reference to com.berniecode.ogre.EntityElement property entity_element" +
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
				"       string property string" +
				"  1. EntityType com.berniecode.ogre.EntityElement" +
				"       string property name",
				clientEngine.getTypeDomain());

	}
	
	public void testCorrectObjectsTransferred() throws Exception {
		ClientEngine clientEngine = createClientEngine();
		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#1" +
			"    bytes=1,2,3" +
			"    entity_element=#1" +
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
			"    string=Shizzle" +
			"  Entity com.berniecode.ogre.EntityElement#1" +
			"    name=Hi!",
			clientEngine);
		
	}
	
	public void testChangesPropagated() throws Exception {
		ClientEngine clientEngine = createClientEngine();
		
		initialEntityObject.setBytes(byteArray(11, 12, 13));
		initialEntityObject.setNullableInt(null);
		initialEntityObject.setNonNullableLong(42L);
		initialEntityObject.setString("Fizzle");
		initialEntityObject.setNonNullableDouble(11770.0);
		initialEntityObject.setNullableDouble(null);
		initialEntityObject.setNullableFloat(1144.0F);
		
		assertEquals(1, msgBridge.getMessageCount());

		// changes propagated
		dataSource.setEntityObjects(initialEntityObject);

		
		assertEquals(2, msgBridge.getMessageCount());
		assertUpdateMessageState(
				"UpdateMessage for object graph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
				"  partial values:" +
				"    EntityUpdate for com.berniecode.ogre.EntityClassWithAllFields#1" +
				"      bytes=11,12,13" +
				"      non_nullable_double=11770.0" +
				"      non_nullable_long=42" +
				"      nullable_double=null" +
				"      nullable_float=1144.0" +
				"      nullable_int=null" +
				"      string=Fizzle",
				msgBridge.getLastUpdateMessage(), dataSource.getTypeDomain());

		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#1" +
			"    bytes=11,12,13" +
			"    entity_element=#1" +
			"    non_nullable_byte=1" +
			"    non_nullable_double=11770.0" +
			"    non_nullable_float=9.0" +
			"    non_nullable_int=5" +
			"    non_nullable_long=42" +
			"    non_nullable_short=3" +
			"    nullable_byte=2" +
			"    nullable_double=null" +
			"    nullable_float=1144.0" +
			"    nullable_int=null" +
			"    nullable_long=8" +
			"    nullable_short=4" +
			"    string=Fizzle" +
			"  Entity com.berniecode.ogre.EntityElement#1" +
			"    name=Hi!",
			clientEngine);
		
		// new objects propagated
		EntityClassWithAllFields newEntityObject = new EntityClassWithAllFields((byte)11, (byte)12, (short)13, (short)14, 15, 16, 17L, 18L, "my bizzle", 19.0F, 20.0F, 21.0, 22.0, byteArray(4, 5, 6), new EntityElement("Bye!"));

		dataSource.setEntityObjects(initialEntityObject, newEntityObject);

		assertEquals(3, msgBridge.getMessageCount());
		assertUpdateMessageState(
				"UpdateMessage for object graph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    EntityUpdate for com.berniecode.ogre.EntityClassWithAllFields#2" +
				"      bytes=4,5,6" +
				"      entity_element=#2" +
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
				"      string=my bizzle" +
				"    EntityUpdate for com.berniecode.ogre.EntityElement#2" +
				"      name=Bye!",
				msgBridge.getLastUpdateMessage(), dataSource.getTypeDomain());
		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#1" +
			"    bytes=11,12,13" +
			"    entity_element=#1" +
			"    non_nullable_byte=1" +
			"    non_nullable_double=11770.0" +
			"    non_nullable_float=9.0" +
			"    non_nullable_int=5" +
			"    non_nullable_long=42" +
			"    non_nullable_short=3" +
			"    nullable_byte=2" +
			"    nullable_double=null" +
			"    nullable_float=1144.0" +
			"    nullable_int=null" +
			"    nullable_long=8" +
			"    nullable_short=4" +
			"    string=Fizzle" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#2" +
			"    bytes=4,5,6" +
			"    entity_element=#2" +
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
			"    string=my bizzle" + 
			"  Entity com.berniecode.ogre.EntityElement#1" +
			"    name=Hi!" + 
			"  Entity com.berniecode.ogre.EntityElement#2" +
			"    name=Bye!",
			clientEngine);
		
		// removes propagated
		dataSource.setEntityObjects(newEntityObject);

		assertEquals(4, msgBridge.getMessageCount());
		assertUpdateMessageState(
				"UpdateMessage for object graph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
				"  deleted entities:" +
				"    EntityDelete for com.berniecode.ogre.EntityClassWithAllFields#1" +
				"    EntityDelete for com.berniecode.ogre.EntityElement#1",
				msgBridge.getLastUpdateMessage(), dataSource.getTypeDomain());
		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph" +
			"  Entity com.berniecode.ogre.EntityClassWithAllFields#2" +
			"    bytes=4,5,6" +
			"    entity_element=#2" +
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
			"    string=my bizzle" + 
			"  Entity com.berniecode.ogre.EntityElement#2" +
			"    name=Bye!",
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
				"    EntityDelete for com.berniecode.ogre.EntityClassWithAllFields#2" +
				"    EntityDelete for com.berniecode.ogre.EntityElement#2",
				msgBridge.getLastUpdateMessage(), dataSource.getTypeDomain());
		 
		assertClientEngineState(
			"ObjectGraph com.berniecode.ogre.test.TypeDomain/TestObjectGraph",
			clientEngine);
	}
	

	public void testNonChangesNotPropagated() throws Exception {
		
		// set all properties to new objects with the same value but different object identities
		assertEquals(1, msgBridge.getMessageCount());
		initialEntityObject.setBytes(byteArray(1, 2, 3));
		initialEntityObject.setNullableByte(new Byte((byte) 2));
		initialEntityObject.setNullableShort(new Short((short) 4));
		initialEntityObject.setNullableInt(new Integer(6));
		initialEntityObject.setNullableLong(new Long(8));
		initialEntityObject.setString(new String("Shizzle"));
		initialEntityObject.setNullableFloat(new Float(10F));
		initialEntityObject.setNullableDouble(new Double(12.0));
		dataSource.setEntityObjects(initialEntityObject);
		if (msgBridge.getMessageCount() != 1) {
			OgreLog.error("Update message incorrectly transmitted:\n" + 
					EDRDescriber.describeUpdateMessage(dataSource.getTypeDomain(), msgBridge.getLastUpdateMessage()));
		}
		assertEquals(1, msgBridge.getMessageCount());
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

	private byte[] byteArray(int ... ints) {
		byte[] bytes = new byte[ints.length];
		for (int i=0; i<ints.length; i++) {
			bytes[i] = (byte) ints[i];
		}
		return bytes;
	}
}
