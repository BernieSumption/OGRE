package com.berniecode.ogre.tcpbridge;

import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.TypeDomain;

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
		TcpBridgeClient bridgeClient = new TcpBridgeClient("localhost", 12345);
		
		TypeDomain td = bridgeClient.loadTypeDomain(TYPE_DOMAIN_ID);
		
		assertEqualsIgnoreWhitespace(EDRDescriber.describeTypeDomain(td), EDRDescriber.describeTypeDomain(typeDomain));
	}


	public void testFetchObjectGraph() throws Exception {
		TcpBridgeClient bridgeClient = new TcpBridgeClient("localhost", 12345);
		
		GraphUpdate objectGraph = bridgeClient.loadObjectGraph(typeDomain, OBJECT_GRAPH_ID);
		
		assertEqualsIgnoreWhitespace(EDRDescriber.describeObjectGraph(serverEngine.getObjectGraph(TYPE_DOMAIN_ID, OBJECT_GRAPH_ID)), EDRDescriber.describeObjectGraph(objectGraph));
	}

}
