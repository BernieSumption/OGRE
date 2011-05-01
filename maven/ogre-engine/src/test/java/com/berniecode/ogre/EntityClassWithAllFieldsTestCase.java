/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre;

import com.berniecode.ogre.enginelib.ClientEngine;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.enginelib.TypeDomain;
import com.berniecode.ogre.server.pojods.DefaultEDRMapper;
import com.berniecode.ogre.server.pojods.PojoDataSource;

public abstract class EntityClassWithAllFieldsTestCase extends OgreTestCase {

	protected InProcessTransport transport;
	protected PojoDataSource dataSource;
	protected EntityClassWithAllFieldsImpl initialEntityObject;
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
	
		transport = new InProcessTransport(dataSource);

		dataSource.setGraphUpdateListener(transport);
		
		typeDomain = dataSource.getTypeDomain();
		
		dataSource.setEntityObjects(initialEntityObject = new EntityClassWithAllFieldsImpl(5, 6, 7L, 8L, "Shizzle", 9.0F, 10.0F, 11.0, 12.0, byteArray(1, 2, 3), new EntityElementImpl("Hi!")));
	}

	protected ClientEngine createClientEngine() throws Exception {
		return createClientEngine(TYPE_DOMAIN_ID);
	}

	protected ClientEngine createClientEngine(String typeDomainId) throws Exception {
		ClientEngine ce = new ClientEngine();
		ce.setTypeDomainId(typeDomainId);
		ce.setTransportAdapter(transport);
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