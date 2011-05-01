/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * Represents an update to a single {@link Entity}
 * 
 * @author Bernie Sumption
 */
public class EntityTest extends OgreTestCase {

	private EntityType entityType0;
	private TypeDomain typeDomain;
	private EntityType entityType1;
	private Property property0;
	private Property property1;

	@Override
	public void doAdditionalSetup() {

		entityType0 = new EntityType("entityType0", new Property[] {
				property0 = new Property("property0", Property.TYPECODE_INT32, false),
				property1 = new Property("property1", Property.TYPECODE_INT64, false) });
		entityType1 = new EntityType("entityType1", new Property[] {});
		typeDomain = new TypeDomain("typeDomain", new EntityType[] { entityType0, entityType1 });
	}

	public void testEntityDiffMessageCreation() {

		Entity from = new Entity(entityType0, 303, new Object[] { Integer.valueOf(128), Long.valueOf(300889) });
		Entity to = new Entity(entityType0, 303, new Object[] { Integer.valueOf(128), Long.valueOf(300888) });

		EntityDiff message = EntityDiff.build(from, to);

		assertEntityUpdateState("partial value for entityType0#303" + "  property1=300888", message, typeDomain);

		assertTrue(message.toString().contains("entityType0#303")); // contains type and id

		assertEquals(message.getRawPropertyValue(property1), 300888L);

		try {
			message.getRawPropertyValue(property0);
			fail("EntityDiff.getValue(index) should fail with an OgreException if "
					+ "the message does not contain an updated value for that index");
		} catch (OgreException e) {
		}
	}

	public void testFailsWithInvalidEntities() {

		Entity from = new Entity(entityType0, 50, new Object[] { Integer.valueOf(128), Long.valueOf(300889) });
		Entity to = new Entity(entityType1, 100, new Object[] {});

		try {
			EntityDiff.build(from, to);
			fail("EntityDiff.build should fail with an OgreException "
					+ "if called with entities of two different types");
		} catch (OgreException e) {
		}
	}

	public void testEntityCreationChecks() {
		try {
			new Entity(entityType0, 0, new Object[] { Integer.valueOf(128), Long.valueOf(300889) });
			fail("Entity constructor should enforce positive entity ids");
		} catch (OgreException e) {
		}

		requireOneLogError(OgreLog.LEVEL_WARN);
		new Entity(entityType0, Long.MAX_VALUE, new Object[] { Integer.valueOf(128), Long.valueOf(300889) });
	}

}
