package com.berniecode.ogre.client;

import java.util.Arrays;
import java.util.List;

import com.berniecode.ogre.EntityClassWithAllFields;
import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;
import com.berniecode.ogre.EntityElement;
import com.berniecode.ogre.enginelib.ClientEngine;

public class ClientFacadeTest extends EntityClassWithAllFieldsTestCase {

	public void testClientFacade() throws Exception {
		ClientEngine clientEngine = createClientEngine();
		
		ClientFacade facade = new ClientFacade(clientEngine);

		assertEquals(2, facade.getEntityClasses().size());
		assertTrue(facade.getEntityClasses().contains(EntityClassWithAllFields.class));
		assertTrue(facade.getEntityClasses().contains(EntityElement.class));

		List<EntityClassWithAllFields> entities = facade.getEntitiesByType(EntityClassWithAllFields.class);
		
		assertEquals(1, entities.size());
		EntityClassWithAllFields entity = entities.get(0);

		assertEquals(initialEntityObject.getNullableInt(), entity.getNullableInt());
		assertEquals(initialEntityObject.getNonNullableInt(), entity.getNonNullableInt());
		assertEquals(initialEntityObject.getNonNullableLong(), entity.getNonNullableLong());
		assertEquals(initialEntityObject.getNullableLong(), entity.getNullableLong());
		assertEquals(initialEntityObject.getString(), entity.getString());
		assertEquals(initialEntityObject.getNonNullableFloat(), entity.getNonNullableFloat());
		assertEquals(initialEntityObject.getNullableFloat(), entity.getNullableFloat());
		assertEquals(initialEntityObject.getNonNullableDouble(), entity.getNonNullableDouble());
		assertEquals(initialEntityObject.getNullableDouble(), entity.getNullableDouble());
		assertTrue(Arrays.equals(initialEntityObject.getBytes(), entity.getBytes()));
		
		
		assertEquals(initialEntityObject.getEntityElement().getName(), entity.getEntityElement().getName());

	}
}
