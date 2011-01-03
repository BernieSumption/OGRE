package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

public class EntityStoreTest extends OgreTestCase {
	
	private EntityType parentType;
	private EntityType childType;
	private TypeDomain typeDomain;
	private ReferenceProperty refProperty;

	@Override
	public void doAdditionalSetup() throws Exception {

		parentType = new EntityType(0, "parentType", new Property[] {
				refProperty = new ReferenceProperty(0, "refProperty", "childType")
		});
		childType = new EntityType(1, "childType", new Property[] {});
		typeDomain = new TypeDomain(TYPE_DOMAIN_ID, new EntityType[] { parentType, childType });
	}
	
	public void testCantReplaceEntity() {
		EntityStore entityStore = new EntityStore(typeDomain);
		
		Entity entity = new Entity(childType, 1, new Object[0]);
		entity.wireEntityReferences(null, null);
		
		entityStore.add(entity);
		
		try {
			entityStore.add(entity);
			fail("EntityStore.add(Entity) should fail if the entity already exists in the store");
		} catch (OgreException e) {}
	}
	
	public void testRemoveEntityNullsReferences() {

		EntityStore entityStore = new EntityStore(typeDomain);

		Entity child = new Entity(childType, 1, new Object[0]);
		Entity parent = new Entity(parentType, 1, new Object[] {1L});
		
		Entity[] entities = new Entity[] {parent, child};
		child.wireEntityReferences(null, entities);
		parent.wireEntityReferences(null, entities);

		entityStore.add(child);
		entityStore.add(parent);

		assertNotNull(parent.getPropertyValue(refProperty));
		entityStore.removeSimilar(child);
		assertNull(parent.getPropertyValue(refProperty));
	}
	
	public void testShouldOnlyAcceptWiredEntities() {

		EntityStore entityStore = new EntityStore(typeDomain);

		Entity child = new Entity(childType, 1, new Object[0]);
		Entity parent = new Entity(parentType, 1, new Object[0]);

		
		try {
			entityStore.add(child);
			entityStore.add(parent);
			fail("EntityStore.add(Entity) should fail for unwired entities");
		} catch (OgreException e) {}
	}

}
