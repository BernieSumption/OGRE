package com.berniecode.ogre.tcpbridge;

import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;
import com.berniecode.ogre.EntityElementImpl;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.server.SerialisedDataSource;

public class TestTcpBridge extends EntityClassWithAllFieldsTestCase {

	private SimpleTcpTransportServer bridgeServer;

	@Override
	protected void doAdditionalSetup() throws Exception {
		super.doAdditionalSetup();
		bridgeServer = new SimpleTcpTransportServer();
		bridgeServer.setPort(12345);
		bridgeServer.setDataSource(new SerialisedDataSource(dataSource, null));
		bridgeServer.initialise();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		bridgeServer.quit();
	}


	public void testFetchTypeDomain() throws Exception {
		SimpleTcpTransportClient bridgeClient = new SimpleTcpTransportClient("localhost", 12345, null);
		
		TypeDomain td = bridgeClient.loadTypeDomain(TYPE_DOMAIN_ID);
		
		assertEqualsIgnoreWhitespace(EDRDescriber.describeTypeDomain(td), EDRDescriber.describeTypeDomain(typeDomain));
	}


	public void testFetchObjectGraph() throws Exception {
		SimpleTcpTransportClient bridgeClient = new SimpleTcpTransportClient("localhost", 12345, null);
		
		GraphUpdate objectGraph = bridgeClient.loadObjectGraph(typeDomain, OBJECT_GRAPH_ID);
		
		assertEqualsIgnoreWhitespace(EDRDescriber.describeObjectGraph(dataSource.createSnapshot()), EDRDescriber.describeObjectGraph(objectGraph));
	}
	
	public void testTransmitGraphUpdates() throws Exception {
		SimpleTcpTransportClient bridgeClient = new SimpleTcpTransportClient("localhost", 12345, null);
		
		MockGraphUpdateListener listener = new MockGraphUpdateListener();
		bridgeClient.subscribeToGraphUpdates(typeDomain, OBJECT_GRAPH_ID, listener);
		
		Thread.sleep(100); // give time for network action
		
		initialEntityObject.setBytes(byteArray(31, 21, 11));
		initialEntityObject.setEntityElement(new EntityElementImpl("lala"));

		dataSource.setEntityObjects(initialEntityObject);

		Thread.sleep(100); // give time for network action
		
		assertNotNull(listener.update);

		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    value for EntityElement#2" +
				"      name=lala" +
				"  partial values:" +
				"    partial value for EntityClassWithAllFields#1" +
				"      bytes=31,21,11" +
				"      entity_element=EntityElement#2" +
				"  deleted entities:" +
				"    delete EntityElement#1",
				listener.update, typeDomain);
	}

}

class MockGraphUpdateListener implements GraphUpdateListener {

	public GraphUpdate update;

	@Override
	public void acceptGraphUpdate(GraphUpdate update) {
		this.update = update;
	}
	
}