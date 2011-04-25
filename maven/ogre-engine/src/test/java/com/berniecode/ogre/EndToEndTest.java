package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;

/**
 * Tests of the OGRE system in its most common configuration running from the server data source
 * through to the client data adapter
 * 
 * @author Bernie Sumption
 */
public class EndToEndTest extends EntityClassWithAllFieldsTestCase {

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
				"TypeDomain TypeDomain" +
				"  0. EntityType EntityClassWithAllFields" +
				"       nullable byte-array property bytes" +
				"       reference to EntityElement property entity_element" +
				"       double property non_nullable_double" +
				"       float property non_nullable_float" +
				"       int32 property non_nullable_int" +
				"       int64 property non_nullable_long" +
				"       nullable double property nullable_double" +
				"       nullable float property nullable_float" +
				"       nullable int32 property nullable_int" +
				"       nullable int64 property nullable_long" +
				"       nullable string property string" +
				"  1. EntityType EntityElement" +
				"       nullable string property name",
				clientEngine.getTypeDomain());

	}
	
	public void testCorrectObjectsTransferred() throws Exception {
		ClientEngine clientEngine = createClientEngine();
		 
		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity EntityClassWithAllFields#1" +
			"    bytes=1,2,3" +
			"    entity_element=EntityElement#1" +
			"    non_nullable_double=11.0" +
			"    non_nullable_float=9.0" +
			"    non_nullable_int=5" +
			"    non_nullable_long=7" +
			"    nullable_double=12.0" +
			"    nullable_float=10.0" +
			"    nullable_int=6" +
			"    nullable_long=8" +
			"    string=Shizzle" +
			"  Entity EntityElement#1" +
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
		initialEntityObject.setEntityElement(new EntityElementImpl("lala"));
		
		assertEquals(1, transport.getMessageCount());

		// changes propagated
		dataSource.setEntityObjects(initialEntityObject);

		
		assertEquals(2, transport.getMessageCount());
		
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    value for EntityElement#2" +
				"      name=lala" +
				"  partial values:" +
				"    partial value for EntityClassWithAllFields#1" +
				"      bytes=11,12,13" +
				"      entity_element=EntityElement#2" +
				"      non_nullable_double=11770.0" +
				"      non_nullable_long=42" +
				"      nullable_double=null" +
				"      nullable_float=1144.0" +
				"      nullable_int=null" +
				"      string=Fizzle" +
				"  deleted entities:" +
				"    delete EntityElement#1",
				transport.getLastGraphUpdate(), typeDomain);

		 
		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity EntityClassWithAllFields#1" +
			"    bytes=11,12,13" +
			"    entity_element=EntityElement#2" +
			"    non_nullable_double=11770.0" +
			"    non_nullable_float=9.0" +
			"    non_nullable_int=5" +
			"    non_nullable_long=42" +
			"    nullable_double=null" +
			"    nullable_float=1144.0" +
			"    nullable_int=null" +
			"    nullable_long=8" +
			"    string=Fizzle" +
			"  Entity EntityElement#2" +
			"    name=lala",
			clientEngine);
		
		// new objects propagated
		EntityClassWithAllFieldsImpl newEntityObject = new EntityClassWithAllFieldsImpl(15, 16, 17L, 18L, "my bizzle", 19.0F, 20.0F, 21.0, 22.0, byteArray(4, 5, 6), new EntityElementImpl("Bye!"));

		dataSource.setEntityObjects(initialEntityObject, newEntityObject);

		assertEquals(3, transport.getMessageCount());
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    value for EntityClassWithAllFields#2" +
				"      bytes=4,5,6" +
				"      entity_element=EntityElement#3" +
				"      non_nullable_double=21.0" +
				"      non_nullable_float=19.0" +
				"      non_nullable_int=15" +
				"      non_nullable_long=17" +
				"      nullable_double=22.0" +
				"      nullable_float=20.0" +
				"      nullable_int=16" +
				"      nullable_long=18" +
				"      string=my bizzle" +
				"    value for EntityElement#3" +
				"      name=Bye!",
				transport.getLastGraphUpdate(), typeDomain);
		 
		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity EntityClassWithAllFields#1" +
			"    bytes=11,12,13" +
			"    entity_element=EntityElement#2" +
			"    non_nullable_double=11770.0" +
			"    non_nullable_float=9.0" +
			"    non_nullable_int=5" +
			"    non_nullable_long=42" +
			"    nullable_double=null" +
			"    nullable_float=1144.0" +
			"    nullable_int=null" +
			"    nullable_long=8" +
			"    string=Fizzle" +
			"  Entity EntityClassWithAllFields#2" +
			"    bytes=4,5,6" +
			"    entity_element=EntityElement#3" +
			"    non_nullable_double=21.0" +
			"    non_nullable_float=19.0" +
			"    non_nullable_int=15" +
			"    non_nullable_long=17" +
			"    nullable_double=22.0" +
			"    nullable_float=20.0" +
			"    nullable_int=16" +
			"    nullable_long=18" +
			"    string=my bizzle" + 
			"  Entity EntityElement#2" +
			"    name=lala" + 
			"  Entity EntityElement#3" +
			"    name=Bye!",
			clientEngine);
		
		// removes propagated
		dataSource.setEntityObjects(newEntityObject);

		assertEquals(4, transport.getMessageCount());
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  deleted entities:" +
				"    delete EntityClassWithAllFields#1" +
				"    delete EntityElement#2",
				transport.getLastGraphUpdate(), typeDomain);
		 
		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity EntityClassWithAllFields#2" +
			"    bytes=4,5,6" +
			"    entity_element=EntityElement#3" +
			"    non_nullable_double=21.0" +
			"    non_nullable_float=19.0" +
			"    non_nullable_int=15" +
			"    non_nullable_long=17" +
			"    nullable_double=22.0" +
			"    nullable_float=20.0" +
			"    nullable_int=16" +
			"    nullable_long=18" +
			"    string=my bizzle" + 
			"  Entity EntityElement#3" +
			"    name=Bye!",
			clientEngine);
		
		// non-changes don't create extra graph updatess
		dataSource.setEntityObjects(newEntityObject);
		assertEquals(4, transport.getMessageCount());
		
		// removes propagated
		dataSource.setEntityObjects();

		assertEquals(5, transport.getMessageCount());
		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  deleted entities:" +
				"    delete EntityClassWithAllFields#2" +
				"    delete EntityElement#3",
				transport.getLastGraphUpdate(), typeDomain);
		 
		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph",
			clientEngine);
	}
	

	public void testNonChangesNotPropagated() throws Exception {
		
		// set all properties to new objects with the same value but different object identities
		assertEquals(1, transport.getMessageCount());
		initialEntityObject.setBytes(byteArray(1, 2, 3));
		initialEntityObject.setNullableInt(new Integer(6));
		initialEntityObject.setNullableLong(new Long(8));
		initialEntityObject.setString(new String("Shizzle"));
		initialEntityObject.setNullableFloat(new Float(10F));
		initialEntityObject.setNullableDouble(new Double(12.0));
		dataSource.setEntityObjects(initialEntityObject);
		if (transport.getMessageCount() != 1) {
			OgreLog.error("Graph updates incorrectly transmitted:\n" + 
					EDRDescriber.describeGraphUpdate(transport.getLastGraphUpdate()));
		}
		assertEquals(1, transport.getMessageCount());
	}
}
