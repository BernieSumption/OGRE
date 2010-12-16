package com.berniecode.ogre.enginelib.client;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.shared.EntityDeleteMessage;
import com.berniecode.ogre.enginelib.shared.EntityDiffMessage;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.EntityUpdate;
import com.berniecode.ogre.enginelib.shared.EntityValueMessage;
import com.berniecode.ogre.enginelib.shared.IntegerPropertyType;
import com.berniecode.ogre.enginelib.shared.ObjectGraphValueMessage;
import com.berniecode.ogre.enginelib.shared.Property;
import com.berniecode.ogre.enginelib.shared.ReferencePropertyType;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.enginelib.shared.ObjectGraphUpdate;

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
	
	private ObjectGraphValueMessage valueMessage;

	private DownloadClientAdapter downloadClientAdapter;
	private MessageClientAdapter messageClientAdapter;

	@Override
	public void doAdditionalSetup() throws Exception {

		downloadClientAdapter = context.mock(DownloadClientAdapter.class); 
		messageClientAdapter = context.mock(MessageClientAdapter.class); 

		entityType0 = new EntityType(0, "entityType0", new Property[] {
				new Property(0, "property0", new IntegerPropertyType(32, false)),
				new Property(1, "property1", new IntegerPropertyType(64, false))
		});
		entityType1 = new EntityType(0, "entityType1", new Property[] {});
		typeDomain = new TypeDomain(TYPE_DOMAIN_ID, new EntityType[] { entityType0, entityType1 });
		
		valueMessage = new ObjectGraphValueMessage(TYPE_DOMAIN_ID, OBJECT_GRAPH_ID, new EntityValueMessage[0]);
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
		ce.acceptUpdateMessage(createUpdateMessage(new EntityValueMessage(0, 200, new Object[] {5, 6L})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity entityType0#200" +
			"    property0=5" +
			"    property1=6",
			ce);
		
		// test entity updated with complete value 
		ce.acceptUpdateMessage(createUpdateMessage(new EntityValueMessage(0, 200, new Object[] {7, 8L})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity entityType0#200" +
			"    property0=7" +
			"    property1=8",
			ce);
		
		// test entity can be updated with partial update
		ce.acceptUpdateMessage(createUpdateMessage(new EntityDiffMessage(0, 200, new Object[] {9, null}, new boolean[] {true, false})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity entityType0#200" +
			"    property0=9" +
			"    property1=8",
			ce);
		
		// test partial value without existing entity causes log error but no exception failure
		
		requireOneLogError(OgreLog.LEVEL_ERROR);
		
		ce.acceptUpdateMessage(createUpdateMessage(new EntityDiffMessage(0, 100, new Object[] {10, null}, new boolean[] {true, false})));
		
	}

	public void testEntityMergingError() throws Exception {

		entityType0 = new EntityType(0, "entityType0", new Property[] {
				new Property(0, "reference", new ReferencePropertyType("entityType0"))
		});
		
		typeDomain = new TypeDomain(TYPE_DOMAIN_ID, new EntityType[] { entityType0 });
		

		ClientEngine ce = createClientEngine();
		ce.initialise();

		requireOneLogError(OgreLog.LEVEL_ERROR);
		
		// test new entity created with complete value
		ce.acceptUpdateMessage(createUpdateMessage(new EntityValueMessage(0, 200, new Object[] {10L})));

		assertClientEngineState(
			"ObjectGraph TypeDomain/TestObjectGraph" +
			"  Entity entityType0#200" +
			"    reference=#10",
			ce);
		
	}

	private ObjectGraphUpdate createUpdateMessage(EntityUpdate... updates) {
		List<EntityValueMessage> valueMessages = new ArrayList<EntityValueMessage>();
		List<EntityDiffMessage> diffMessages = new ArrayList<EntityDiffMessage>();
		List<EntityDeleteMessage> deleteMessages = new ArrayList<EntityDeleteMessage>();
		for (EntityUpdate update: updates) {
			if (update instanceof EntityValueMessage) {
				valueMessages.add((EntityValueMessage) update);
			}
			else if (update instanceof EntityDiffMessage) {
				diffMessages.add((EntityDiffMessage) update);
			}
		}
		return new ObjectGraphUpdate(TYPE_DOMAIN_ID, OBJECT_GRAPH_ID,
				valueMessages.toArray(new EntityValueMessage[0]),
				diffMessages.toArray(new EntityDiffMessage[0]),
				deleteMessages.toArray(new EntityDeleteMessage[0]));
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
		    
		    oneOf (downloadClientAdapter).loadObjectGraph(TYPE_DOMAIN_ID, OBJECT_GRAPH_ID);
		    will(returnValue(valueMessage));
		    
		    oneOf (messageClientAdapter).subscribeToUpdateMessages(TYPE_DOMAIN_ID, OBJECT_GRAPH_ID, ce);
		}});
		return ce;
	}
}
