package com.berniecode.ogre.enginelib.client;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.TestTypeDomain;
import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.shared.TypeDomain;

/**
 * This class implements the core OGRE client behaviour as defined in the OGRE protocol
 * specification
 * 
 * @author Bernie Sumption
 */
public class ClientEngineTest extends TestCase {

	public void testInitialiseRequiresDependencies() throws Exception {
		ClientEngine ce = new ClientEngine();
		boolean exceptionThrown = false;
		try {
			ce.initialise();
		} catch (OgreException ex) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

	public void testDependenciesLockedAfterInitialise() throws Exception {
		ClientEngine ce = configureClientEngine(new ClientEngine());
		ce.initialise();

		boolean exceptionThrown = false;
		try {
			configureClientEngine(ce);
		} catch (OgreException ex) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

	public void testFetchTypeDomainOverDownloadBridge() throws Exception {
		TypeDomain td = new EmptyTypeDomain();
		ClientEngine ce = configureClientEngine(new ClientEngine());
		ce.setDownloadAdapter(new MockDownloadClientAdapter(td));
		ce.initialise();
		assertEquals("ClientEngine.getTypeDomain() should return the type domain fetched over the client bridge.", td,
				ce.getTypeDomain());
	}

	private ClientEngine configureClientEngine(ClientEngine ce) {
		ce.setDownloadAdapter(new MockDownloadClientAdapter(null));
		ce.setTypeDomainId(TestTypeDomain.TYPE_DOMAIN_ID);
		return ce;
	}
}
