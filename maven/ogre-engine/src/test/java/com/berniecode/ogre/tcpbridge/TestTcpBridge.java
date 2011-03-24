package com.berniecode.ogre.tcpbridge;

import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;

public class TestTcpBridge extends EntityClassWithAllFieldsTestCase {
	
	private TcpBridgeServer bridgeServer;

	@Override
	protected void doAdditionalSetup() throws Exception {
		super.doAdditionalSetup();
		bridgeServer = new TcpBridgeServer();
		bridgeServer.setPort(12345);
		bridgeServer.setServerEngine(serverEngine);
		bridgeServer.initialise();
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		bridgeServer.quit();
	}


	public void testFetchTypeDomain() throws Exception {
		System.err.println("Foo");
	}

}
