package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.client.ClientEngine;
import com.berniecode.ogre.enginelib.server.ServerEngine;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;
import com.berniecode.ogre.server.pojods.PojoDataSource;

public abstract class EntityClassWithAllFieldsTestCase extends OgreTestCase {

	private InProcessDownloadBridge dlBridge;
	protected InProcessMessageBridge msgBridge;
	protected PojoDataSource dataSource;
	private ServerEngine serverEngine;
	protected EntityClassWithAllFields initialEntityObject;
	protected TypeDomain typeDomain;

	public EntityClassWithAllFieldsTestCase() {
		super();
	}

	public EntityClassWithAllFieldsTestCase(String name) {
		super(name);
	}

	@Override
	protected void doAdditionalSetup() throws Exception {
		OgreLog.info("EndToEndTest.setUp() Creating new OGRE server");
		dataSource = new PojoDataSource();
		dataSource.setEDRMapper(new DefaultEDRMapper(TYPE_DOMAIN_ID, EntityClassWithAllFields.class, EntityElement.class));
		dataSource.setObjectGraphId(OBJECT_GRAPH_ID);
		dataSource.initialise();
		
		typeDomain = dataSource.getTypeDomain();
	
		serverEngine = new ServerEngine();
		serverEngine.setDataSource(dataSource);
		serverEngine.setMessageAdapter(msgBridge = new InProcessMessageBridge());
		serverEngine.initialise();
	
		dlBridge = new InProcessDownloadBridge(serverEngine);
		
		dataSource.setEntityObjects(initialEntityObject = new EntityClassWithAllFields((byte)1, (byte)2, (short)3, (short)4, 5, 6, 7L, 8L, "Shizzle", 9.0F, 10.0F, 11.0, 12.0, byteArray(1, 2, 3), new EntityElement("Hi!")));
	}

	protected ClientEngine createClientEngine() throws Exception {
		return createClientEngine(TYPE_DOMAIN_ID);
	}

	protected ClientEngine createClientEngine(String typeDomainId) throws Exception {
		ClientEngine ce = new ClientEngine();
		ce.setTypeDomainId(typeDomainId);
		ce.setDownloadAdapter(dlBridge);
		ce.setMessageAdapter(msgBridge);
		ce.setObjectGraphId(OBJECT_GRAPH_ID);
		ce.initialise();
		return ce;
	}

	protected byte[] byteArray(int ... ints) {
		byte[] bytes = new byte[ints.length];
		for (int i=0; i<ints.length; i++) {
			bytes[i] = (byte) ints[i];
		}
		return bytes;
	}

}