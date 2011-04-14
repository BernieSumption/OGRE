package com.berniecode.ogre.tcpbridge;

import com.berniecode.ogre.EntityClassWithAllFieldsTestCase;
import com.berniecode.ogre.EntityElementImpl;
import com.berniecode.ogre.enginelib.EDRDescriber;
import com.berniecode.ogre.enginelib.GraphUpdate;
import com.berniecode.ogre.enginelib.GraphUpdateListener;
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
	
	public void testTransmitGraphUpdates() throws Exception {
		TcpBridgeClient bridgeClient = new TcpBridgeClient("localhost", 12345);
		
		MockGraphUpdateListener listener = new MockGraphUpdateListener();
		bridgeClient.subscribeToGraphUpdates(typeDomain, OBJECT_GRAPH_ID, listener);
		
		Thread.sleep(500);
		
		initialEntityObject.setBytes(byteArray(11, 12, 13));
		initialEntityObject.setNullableInt(null);
		initialEntityObject.setNonNullableLong(42L);
		initialEntityObject.setString("Fizzle");
		initialEntityObject.setNonNullableDouble(11770.0);
		initialEntityObject.setNullableDouble(null);
		initialEntityObject.setNullableFloat(1144.0F);
		initialEntityObject.setEntityElement(new EntityElementImpl("lala"));

		dataSource.setEntityObjects(initialEntityObject);
		
		assertNotNull(listener.update);

		assertGraphUpdateState(
				"GraphUpdate for object graph TypeDomain/TestObjectGraph" +
				"  complete values:" +
				"    value for EntityElement#2" +
				"      name=lala" +
				"  partial values:" +
				"    partial value for EntityClassWithAllFields#1" +
				"      bytes=11,12,13" +
				"      entity_element=EntityElement#2" +
				"      non_nullable_double=11770.0" +
				"      non_nullable_long=42" +
				"      nullable_double=null" +
				"      nullable_float=1144.0" +
				"      nullable_int=null" +
				"      string=Fizzle" +
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