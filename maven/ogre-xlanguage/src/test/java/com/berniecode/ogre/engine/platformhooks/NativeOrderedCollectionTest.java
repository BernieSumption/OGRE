package com.berniecode.ogre.engine.platformhooks;

import junit.framework.TestCase;

import com.berniecode.ogre.engine.shared.OrderedCollection;

/**
 * A Java platform implementation of the {@link OrderedCollection} interface
 * 
 * @author Bernie Sumption
 */
public class NativeOrderedCollectionTest extends TestCase {

	public void testBasicOperations() {
		NativeOrderedCollection<String> noc = new NativeOrderedCollection<String>();

		assertEquals(noc.size(), 0);

		noc.push("first");

		assertEquals(noc.size(), 1);
		assertEquals(noc.get(0), "first");

		noc.push("second");

		assertEquals(noc.size(), 2);
		assertEquals(noc.get(0), "first");
		assertEquals(noc.get(1), "second");

		assertEquals(noc.indexOf("first"), 0);
		assertEquals(noc.indexOf("second"), 1);

		// should not affect indexOf
		noc.push("second");
		assertEquals(noc.indexOf("second"), 1);

	}

}
