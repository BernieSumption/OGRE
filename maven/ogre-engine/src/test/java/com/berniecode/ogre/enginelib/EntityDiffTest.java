package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * Represents an update to a single {@link Entity}
 *
 * @author Bernie Sumption
 */
public class EntityDiffTest extends OgreTestCase {
	
	private EntityType entityType0;
	private TypeDomain typeDomain;
	private EntityType entityType1;
	private Property property0;
	private Property property1;

	@Override
	public void doAdditionalSetup() {

		entityType0 = new EntityType("entityType0", new Property[] {
				property0 = new Property("property0", Property.TYPECODE_INT32, false),
				property1 = new Property("property1", Property.TYPECODE_INT64, false)
		});
		entityType1 = new EntityType("entityType1", new Property[] {});
		typeDomain = new TypeDomain("typeDomain", new EntityType[] { entityType0, entityType1 });
	}

	public void testEntityDiffMessageCreation() {

		Entity from = new Entity(entityType0, 303, new Object[] { Integer.valueOf(128), Long.valueOf(300889) });
		Entity to = new Entity(entityType0, 303, new Object[] { Integer.valueOf(128), Long.valueOf(300888) });
		
		EntityDiff message = EntityDiff.build(from, to);
		
		assertEntityUpdateState(
				"PartialRawPropertyValueSet for entityType0#303" +
				"  property1=300888",
				message, typeDomain);

		assertTrue(message.toString().contains("entityType0#303")); // contains type and id

		assertEquals(message.getRawPropertyValue(property1), 300888L);
		
		try {
			message.getRawPropertyValue(property0);
			fail("EntityDiff.getValue(index) should fail with an OgreException if " +
					"the message does not contain an updated value for that index");
		} catch (OgreException e) {}
	}
	
	public void testFailsWithInvalidEntities() {
		
		Entity from = new Entity(entityType0, 50, new Object[] { Integer.valueOf(128), Long.valueOf(300889) });
		Entity to = new Entity(entityType1, 100, new Object[] {  });
		
		try {
			EntityDiff.build(from, to);
			fail("EntityDiff.build should fail with an OgreException " +
					"if called with entities of two different types");
		} catch (OgreException e) {
		}
	}

}
