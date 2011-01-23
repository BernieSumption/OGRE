package com.berniecode.ogre.client;

import java.util.Arrays;
import java.util.List;

import com.berniecode.ogre.EntityClassWithAllFields;
import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;
import com.berniecode.ogre.EntityElement;
import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.Entity;

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

		Object o1 = facade.getEntity(EntityClassWithAllFields.class, 1);
		Object o2 = facade.getEntity(EntityClassWithAllFields.class, 1);
		
		assertNotNull(o1);
		assertSame("subsequent calls to getEntity should return the same proxy object", o1, o2);
		
		
	}

	public void testProxiedEntity() throws Exception {
		ClientEngine clientEngine = createClientEngine();
		ClientFacade facade = new ClientFacade(clientEngine);
		
		Object o = facade.getEntity(EntityClassWithAllFields.class, 1);
		Entity e = ((EntityProxy) o).getProxiedEntity();
		
		assertEquals(o.toString(), e.toString());
		
	}
	
	
	public void testFailsOnIncorrectArguments() throws Exception {

		ClientEngine clientEngine = createClientEngine();
		ClientFacade facade = new ClientFacade(clientEngine);
		
		try {
			facade.getEntity(Integer.class, 1);
			fail("getEntitiesByType() should fail when called with a non-mapped class");
		} catch (ClientFacadeException e) {}
		
		try {
			facade.getEntitiesByType(Integer.class);
			fail("getEntitiesByType() should fail when called with a non-mapped class");
		} catch (ClientFacadeException e) {}
		

		EntityClassWithAllFields o = facade.getEntity(EntityClassWithAllFields.class, 1);
		try {
			o.nonGetterMethod();
			fail("o.x() should fail if x is not a mapped getter method");
		} catch (ClientFacadeException e) {}
	}
}

interface EntityAccessor extends EntityClassWithAllFields {
	
	public EntityElement[] getBackReferences();
}
