package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.platformhooks.IOFailureException;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;

/**
 * A ClientEngineTest configures and executes the replication of a single object graph. It is the
 * frontend of the cross-language OGRE client, and will typically not be used directly but should be
 * wrapped in a suitable language-specific facade.
 * 
 * @author Bernie Sumption
 */
public class ClientEngineTest extends OgreTestCase {

	public void testInitialisation() throws NoSuchThingException, IOFailureException {
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
		
		//TODO simulate IOFailureException
		
		//TODO use JMock to provide dependencies and test (A) that double initialisation is OK and (B) that setTypeDoaminId() after initialise() is not
	}
}
