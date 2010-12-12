package com.berniecode.ogre;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.client.ClientEngine;
import com.berniecode.ogre.enginelib.shared.EDRDescriber;
import com.berniecode.ogre.enginelib.shared.EntityUpdate;
import com.berniecode.ogre.enginelib.shared.ObjectGraphValueMessage;
import com.berniecode.ogre.enginelib.shared.TypeDomain;
import com.berniecode.ogre.enginelib.shared.UpdateMessage;

public abstract class OgreTestCase extends TestCase {

	protected static final String TYPE_DOMAIN_ID = "com.berniecode.ogre.test.TypeDomain";
	protected static final String OBJECT_GRAPH_ID = "TestObjectGraph";

	public OgreTestCase() {
		super();
	}

	public OgreTestCase(String name) {
		super(name);
	}

	protected void assertEqualsIgnoreWhitespace(String expected, String actual) {
		if (expected != null) {
			expected = expected.replaceAll("\\s+", "\n");
		}
		if (actual != null) {
			actual = actual.replaceAll("\\s+", "\n");
		}
		assertEquals(expected, actual);
	}
	
	protected void assertTypeDomainState(String expected, TypeDomain typeDomain) {
		assertEqualsIgnoreWhitespace(expected, EDRDescriber.describeTypeDomain(typeDomain));
	}
	
	protected void assertObjectGraphState(String expected, ObjectGraphValueMessage objectGraph, TypeDomain typeDomain) {
		assertEqualsIgnoreWhitespace(expected, EDRDescriber.describeObjectGraph(typeDomain, objectGraph));
	}
	
	protected void assertUpdateMessageState(String expected, UpdateMessage updateMessage, TypeDomain typeDomain) {
		assertEqualsIgnoreWhitespace(expected, EDRDescriber.describeUpdateMessage(typeDomain, updateMessage));
	}
	
	protected void assertEntityUpdateState(String expected, EntityUpdate entityUpdate, TypeDomain typeDomain) {
		assertEqualsIgnoreWhitespace(expected, EDRDescriber.describeEntityUpdate(typeDomain, entityUpdate));
	}
	
	protected void assertClientEngineState(String expected, ClientEngine actual) {
		assertObjectGraphState(expected, actual.createSnapshot(), actual.getTypeDomain());
	}

}