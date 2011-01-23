package com.berniecode.ogre.enginelib;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.InvalidGraphUpdateException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

public class ClientEngineTest extends OgreTestCase {
	
	private EntityType parentType;
	private ReferenceProperty refProperty;
	private EntityType childType;
	private TypeDomain typeDomain;
	
	private GraphUpdate initialValueUpdate;

	private DownloadClientAdapter downloadClientAdapter;
	private MessageClientAdapter messageClientAdapter;
	private Property parentName;
	private Property childName;

	@Override
	public void doAdditionalSetup() throws Exception {

		downloadClientAdapter = context.mock(DownloadClientAdapter.class); 
		messageClientAdapter = context.mock(MessageClientAdapter.class); 

		parentType = new EntityType("parentType", new Property[] {
				parentName = new Property("num", Property.TYPECODE_STRING, false),
				refProperty = new ReferenceProperty("ref", "childType")
		});
		childType = new EntityType("childType", new Property[] {
				childName = new Property("name", Property.TYPECODE_STRING, true)
		});
		typeDomain = new TypeDomain(TYPE_DOMAIN_ID, new EntityType[] { parentType, childType });
		
		initialValueUpdate = new GraphUpdate(typeDomain, OBJECT_GRAPH_ID, null, null, null);
	}

	public void testInitialiseWithoutDependencies() throws NoSuchThingException {
		
		ClientEngine ce = new ClientEngine();
		try {
			ce.initialise();
			fail("ClientEngine.initialise() should throw an exception if initialised without dependencies");
		} catch (InitialisationException e) {}
		
		ce = new ClientEngine();
		try {
			ce.getTypeDomain();
			fail("ClientEngine.getTypeDomain() should throw an exception if called before initialise()");
		} catch (InitialisationException e) {}
		
	}
	
	public void testInitialiseWithDependencies() throws Exception {
		
		ClientEngine ce = createClientEngine();

		ce.initialise();
		ce.initialise(); // second call shouldn't result in more calls to loadTypeDomain et al.

		context.assertIsSatisfied();

		try {
			ce.setDownloadAdapter(null);
			fail("ClientEngine.setDownloadAdapter should fail after ClientEngine.initialise() is called");
		} catch (InitialisationException e) {}
		try {
			ce.setMessageAdapter(null);
			fail("ClientEngine.setMessageAdapter should fail after ClientEngine.initialise() is called");
		} catch (InitialisationException e) {}
		try {
			ce.setTypeDomainId(null);
			fail("ClientEngine.setTypeDomainId should fail after ClientEngine.initialise() is called");
		} catch (InitialisationException e) {}
		try {
			ce.setObjectGraphId(null);
			fail("ClientEngine.setObjectGraphId should fail after ClientEngine.initialise() is called");
		} catch (InitialisationException e) {}
		
	}

	public void testEntityMerging() throws Exception {

		ClientEngine ce = createClientEngine();
		ce.initialise();
		
		
		// test new entity created with complete value
		ce.acceptGraphUpdate(createGraphUpdate(new Entity(parentType, 200, new Object[] {"5", null})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity parentType#200" +
			"    num=5" +
			"    ref=null",
			ce);
		
		// test creating a new entity with same type and id fails
		try {
			ce.acceptGraphUpdate(createGraphUpdate(new Entity(parentType, 200, new Object[] {"7", null})));
			fail("acceptGraphUpdate() should fail with an entity that already exists in the cient engine");
		} catch (InvalidGraphUpdateException e) {}
		
		// test entity can be updated with partial update
		ce.acceptGraphUpdate(createGraphUpdate(new EntityDiff(parentType, 200, new Object[] {"9", null}, new boolean[] {true, false})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity parentType#200" +
			"    num=9" +
			"    ref=null",
			ce);
		
		// test partial value without existing entity causes log error but no exception failure
		
		requireOneLogError(OgreLog.LEVEL_ERROR);
		
		ce.acceptGraphUpdate(createGraphUpdate(new EntityDiff(parentType, 100, new Object[] {"10", null}, new boolean[] {true, false})));
		
	}

	public void testEntityMergingError() throws Exception {
		

		ClientEngine ce = createClientEngine();
		ce.initialise();

		try {
			new Entity(parentType, 200, new Object[] {10});
			fail("new Entity() should fail when given an initialValues array with too new values");
		} catch (OgreException e) {}

		try {
			//TODO check more kinds of validation - graph updates with wrong runtime type, null values for not-null properties, dupicate ids in graph update
			ce.acceptGraphUpdate(createGraphUpdate(new EntityValue(parentType, 20, new Object[] {"0", 10L})));
			fail("acceptGraphUpdate() should fail when given a GraphUpdate that references a non-=existant entity");
		} catch (OgreException e) {}
		
		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph",
			ce);
		
	}
	
	public void testBackReferences() throws Exception {
		ClientEngine ce = createClientEngine();
		ce.initialise();
		
		ce.acceptGraphUpdate(createGraphUpdate(
				new EntityValue(parentType, 1, new Object[] {"to dave", 1L}),
				new EntityValue(parentType, 2, new Object[] {"to none", null}),
				new EntityValue(parentType, 3, new Object[] {"to sally", 2L}),
				new EntityValue(parentType, 4, new Object[] {"to dave also", 1L}),
				new EntityValue(parentType, 5, new Object[] {"to none also", null}),
				new EntityValue(childType, 1, new Object[] {"dave"}),
				new EntityValue(childType, 2, new Object[] {"sally"})));
		
		Entity dave = ce.getEntityByTypeAndId(childType, 1);
		
		try {
			ce.getReferencesTo(dave, new ReferenceProperty("random", "foo"));
			fail("Entity.getBackReferences() should fail with a property that does not reference the entity type in question");
		} catch (OgreException e) {}
		
		Entity[] references = ce.getReferencesTo(dave, refProperty);
		
		assertNotNull(references);
		assertEquals(2, references.length);
		assertEquals(references[0].getPropertyValue(parentName), "to dave");
		assertEquals(references[1].getPropertyValue(parentName), "to dave also");
	}

	private GraphUpdate createGraphUpdate(EntityReference... updates) {
		List<RawPropertyValueSet> valueMessages = new ArrayList<RawPropertyValueSet>();
		List<PartialRawPropertyValueSet> diffMessages = new ArrayList<PartialRawPropertyValueSet>();
		for (EntityReference update: updates) {
			if (update instanceof Entity) {
				valueMessages.add((Entity) update);
			}
			else if (update instanceof EntityDiff) {
				diffMessages.add((EntityDiff) update);
			}
			else if (update instanceof EntityValue) {
				valueMessages.add((EntityValue) update);
			}
			else {
				throw new OgreException("Invalid argument to createGraphUpdate() of type " + update.getClass());
			}
		}
		return new GraphUpdate(typeDomain, OBJECT_GRAPH_ID,
				valueMessages.toArray(new RawPropertyValueSet[0]),
				diffMessages.toArray(new PartialRawPropertyValueSet[0]),
				null);
	}

	private ClientEngine createClientEngine() throws NoSuchThingException {
		final ClientEngine ce = new ClientEngine();
		ce.setDownloadAdapter(downloadClientAdapter);
		ce.setMessageAdapter(messageClientAdapter);
		ce.setTypeDomainId(TYPE_DOMAIN_ID);
		ce.setObjectGraphId(OBJECT_GRAPH_ID);

		context.checking(new Expectations() {{
		    oneOf (downloadClientAdapter).loadTypeDomain(TYPE_DOMAIN_ID);
		    will(returnValue(typeDomain));
		    
		    oneOf (downloadClientAdapter).loadObjectGraph(typeDomain, OBJECT_GRAPH_ID);
		    will(returnValue(initialValueUpdate));
		    
		    oneOf (messageClientAdapter).subscribeToGraphUpdates(typeDomain, OBJECT_GRAPH_ID, ce);
		}});
		return ce;
	}
}
