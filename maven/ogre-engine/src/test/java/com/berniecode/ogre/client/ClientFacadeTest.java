package com.berniecode.ogre.client;

import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;
import com.berniecode.ogre.enginelib.ClientEngine;

public class ClientFacadeTest extends EntityClassWithAllFieldsTestCase {

	public void testClientFacade() throws Exception {
		ClientEngine clientEngine = createClientEngine();
		
		ClientFacade facade = ClientFacadeFactory.createFacade(ClientFacade.class, clientEngine);

		System.out.println(facade.getEntityClasses());
		System.out.println(facade.getEntityClasses());
	}
}
