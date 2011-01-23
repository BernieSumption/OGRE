package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

public class ClientEngineBackreferenceTest extends OgreTestCase {
	
	private EntityType parentType;
	private EntityType childType;
	private TypeDomain typeDomain;
	private ReferenceProperty refProperty;

	@Override
	public void doAdditionalSetup() throws Exception {

		parentType = new EntityType("parentType", new Property[] {
				refProperty = new ReferenceProperty("refProperty", "childType")
		});
		childType = new EntityType("childType", new Property[] {});
		typeDomain = new TypeDomain(TYPE_DOMAIN_ID, new EntityType[] { parentType, childType });
	}
	
	public void testCantReplaceEntity() {
		EntityStore entityStore = new EntityStore(typeDomain);
		
		Entity entity = new Entity(childType, 1, new Object[0]);
		
		entityStore.add(entity);
		
		try {
			entityStore.add(entity);
			fail("EntityStore.add(Entity) should fail if the entity already exists in the store");
		} catch (OgreException e) {}
	}
//	
//
//
//	private ClientEngine createClientEngine() throws NoSuchThingException {
//		final ClientEngine ce = new ClientEngine();
//		ce.setDownloadAdapter(downloadClientAdapter);
//		ce.setMessageAdapter(messageClientAdapter);
//		ce.setTypeDomainId(TYPE_DOMAIN_ID);
//		ce.setObjectGraphId(OBJECT_GRAPH_ID);
//
//		context.checking(new Expectations() {{
//		    oneOf (downloadClientAdapter).loadTypeDomain(TYPE_DOMAIN_ID);
//		    will(returnValue(typeDomain));
//		    
//		    oneOf (downloadClientAdapter).loadObjectGraph(typeDomain, OBJECT_GRAPH_ID);
//		    will(returnValue(initialValueUpdate));
//		    
//		    oneOf (messageClientAdapter).subscribeToGraphUpdates(typeDomain, OBJECT_GRAPH_ID, ce);
//		}});
//		return ce;
//	}

}
