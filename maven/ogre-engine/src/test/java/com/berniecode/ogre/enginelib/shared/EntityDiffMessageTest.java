package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * Represents an update to a single {@link Entity}
 *
 * @author Bernie Sumption
 */
public class EntityDiffMessageTest extends OgreTestCase {
	
	private EntityType entityType0;
	private TypeDomain typeDomain;
	private EntityType entityType1;

	@Override
	public void doAdditionalSetup() {

		entityType0 = new EntityType(0, "entityType0", new Property[] {
				new Property(0, "property0", new IntegerPropertyType(32, false)),
				new Property(1, "property1", new IntegerPropertyType(64, false))
		});
		entityType1 = new EntityType(0, "entityType1", new Property[] {});
		typeDomain = new TypeDomain("typeDomain", new EntityType[] { entityType0, entityType1 });
	}

	public void testEntityDiffMessageCreation() {

		Entity from = new Entity(entityType0, 303, new Object[] { Integer.valueOf(128), Long.valueOf(300889) });
		Entity to = new Entity(entityType0, 303, new Object[] { Integer.valueOf(128), Long.valueOf(300888) });
		
		EntityDiffMessage message = EntityDiffMessage.build(from, to);
		
		assertEntityUpdateState(
				"EntityUpdate for entityType0#303" +
				"  property1=300888",
				message, typeDomain);

		assertTrue(message.toString().matches(".*\\b0\\b.*")); // contains entityTypeIndex
		assertTrue(message.toString().matches(".*\\b303\\b.*")); // contains id

		assertEquals(message.getValue(1), 300888L);
		
		try {
			message.getValue(0);
			fail("EntityDiffMessage.getValue(index) should fail with an OgreException if " +
					"the message does not contain an updated value for that index");
		} catch (OgreException e) {}
	}
	
	public void testFailsWithInvalidEntities() {
		
		Entity from = new Entity(entityType0, 50, new Object[] { Integer.valueOf(128), Long.valueOf(300889) });
		Entity to = new Entity(entityType1, 100, new Object[] {  });
		
		try {
			EntityDiffMessage.build(from, to);
			fail("EntityDiffMessage.build should fail with an OgreException " +
					"if called with entities of two different types");
		} catch (OgreException e) {
		}
	}

}
