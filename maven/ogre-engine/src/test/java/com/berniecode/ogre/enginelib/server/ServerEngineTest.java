package com.berniecode.ogre.enginelib.server;

import com.berniecode.ogre.OgreTestCase;
import com.berniecode.ogre.enginelib.platformhooks.InitialisationException;
import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;

/**
 * A ServerEngineTest provides access to any number of object graphs belonging to any number of type
 * domains
 * 
 * @author Bernie Sumption
 */
public class ServerEngineTest extends OgreTestCase {

	public void testInitialisation() throws NoSuchThingException {
		ServerEngine se = new ServerEngine();
		try {
			se.initialise();
			fail("ClientEngine.initialise() should throw an exception if initialised without dependencies");
		} catch (InitialisationException e) {}
		
		se = new ServerEngine();
		try {
			se.getObjectGraph(TYPE_DOMAIN_ID, OBJECT_GRAPH_ID);
			fail("ServerEngine.getObjectGraph() should throw an exception if called before initialise()");
		} catch (InitialisationException e) {}
		

		se = new ServerEngine();
		se.setDataSources(new DataSource[0]);
		se.initialise();
		se.initialise(); // initialise twice should not cause issues
		try {
			se.getObjectGraph(TYPE_DOMAIN_ID, OBJECT_GRAPH_ID);
			fail("ServerEngine.getObjectGraph() with non-existant graph ids should throw a NoSuchThingException");
		} catch (NoSuchThingException e) {}
	}

}
