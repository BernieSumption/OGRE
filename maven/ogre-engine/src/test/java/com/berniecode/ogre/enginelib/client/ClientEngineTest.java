package com.berniecode.ogre.enginelib.client;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.DownloadClientAdapter;
import com.berniecode.ogre.enginelib.Entity;
import com.berniecode.ogre.enginelib.EntityDelete;
import com.berniecode.ogre.enginelib.EntityDiff;
import com.berniecode.ogre.enginelib.EntityReference;
import com.berniecode.ogre.enginelib.EntityType;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.IntegerProperty;
import com.berniecode.ogre.enginelib.MessageClientAdapter;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.Property;
import com.berniecode.ogre.enginelib.ReferenceProperty;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A ClientEngineTest configures and executes the replication of a single object graph. It is the
 * frontend of the cross-language OGRE client, and will typically not be used directly but should be
 * wrapped in a suitable language-specific facade.
 * 
 * @author Bernie Sumption
 */
public class ClientEngineTest extends OgreTestCase {
	
	private EntityType entityType0;
	private EntityType entityType1;
	private TypeDomain typeDomain;
	
	private GraphUpdate initialValueUpdate;

	private DownloadClientAdapter downloadClientAdapter;
	private MessageClientAdapter messageClientAdapter;

	@Override
	public void doAdditionalSetup() throws Exception {

		downloadClientAdapter = context.mock(DownloadClientAdapter.class); 
		messageClientAdapter = context.mock(MessageClientAdapter.class); 

		entityType0 = new EntityType(0, "entityType0", new Property[] {
				new IntegerProperty(0, "property0", 32, false),
				new IntegerProperty(1, "property1", 64, false)
		});
		entityType1 = new EntityType(0, "entityType1", new Property[] {});
		typeDomain = new TypeDomain(TYPE_DOMAIN_ID, new EntityType[] { entityType0, entityType1 });
		
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
		ce.acceptGraphUpdate(createGraphUpdate(new Entity(entityType0, 200, new Object[] {5, 6L})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity entityType0#200" +
			"    property0=5" +
			"    property1=6",
			ce);
		
		// test entity updated with complete value 
		ce.acceptGraphUpdate(createGraphUpdate(new Entity(entityType0, 200, new Object[] {7, 8L})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity entityType0#200" +
			"    property0=7" +
			"    property1=8",
			ce);
		
		// test entity can be updated with partial update
		ce.acceptGraphUpdate(createGraphUpdate(new EntityDiff(entityType0, 200, new Object[] {9, null}, new boolean[] {true, false})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity entityType0#200" +
			"    property0=9" +
			"    property1=8",
			ce);
		
		// test partial value without existing entity causes log error but no exception failure
		
		requireOneLogError(OgreLog.LEVEL_ERROR);
		
		ce.acceptGraphUpdate(createGraphUpdate(new EntityDiff(entityType0, 100, new Object[] {10, null}, new boolean[] {true, false})));
		
	}

	public void testEntityMergingError() throws Exception {

		entityType0 = new EntityType(0, "entityType0", new Property[] {
				new ReferenceProperty(0, "reference", "entityType0")
		});
		
		typeDomain = new TypeDomain(TYPE_DOMAIN_ID, new EntityType[] { entityType0 });
		

		ClientEngine ce = createClientEngine();
		ce.initialise();
		
		try {
			ce.acceptGraphUpdate(createGraphUpdate(new Entity(entityType0, 200, new Object[] {10L})));
			fail("acceptGraphUpdate() should fail when given an entity that references a non-existant entity");
		} catch (OgreException e) {}
		
		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph",
			ce);
		
	}

	private GraphUpdate createGraphUpdate(EntityReference... updates) {
		List<Entity> valueMessages = new ArrayList<Entity>();
		List<EntityDiff> diffMessages = new ArrayList<EntityDiff>();
		List<EntityDelete> deleteMessages = new ArrayList<EntityDelete>();
		for (EntityReference update: updates) {
			if (update instanceof Entity) {
				valueMessages.add((Entity) update);
			}
			else if (update instanceof EntityDiff) {
				diffMessages.add((EntityDiff) update);
			}
		}
		return new GraphUpdate(typeDomain, OBJECT_GRAPH_ID,
				valueMessages.toArray(new Entity[0]),
				diffMessages.toArray(new EntityDiff[0]),
				deleteMessages.toArray(new EntityDelete[0]));
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
