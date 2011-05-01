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

public class EntityStoreTest extends OgreTestCase {

	private EntityType parentType;
	private EntityType childType;
	private TypeDomain typeDomain;
	private ReferenceProperty refProperty;

	@Override
	public void doAdditionalSetup() throws Exception {

		parentType = new EntityType("parentType", new Property[] { refProperty = new ReferenceProperty("refProperty",
				"childType") });
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
		} catch (OgreException e) {
		}
	}

	public void testRemoveEntityNullsReferences() {

		EntityStore entityStore = new EntityStore(typeDomain);

		Entity child = new Entity(childType, 1, new Object[0]);
		Entity parent = new Entity(parentType, 1, new Object[] { child });

		entityStore.add(child);
		entityStore.add(parent);

		assertNotNull(parent.getPropertyValue(refProperty));
		entityStore.removeSimilar(child);
		assertNull(parent.getPropertyValue(refProperty));
	}

}
