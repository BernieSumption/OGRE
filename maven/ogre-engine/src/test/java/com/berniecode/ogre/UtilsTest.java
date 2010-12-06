package com.berniecode.ogre;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {

	public void testGetPropertyNameForGetter() {

		assertEquals("x", Utils.getPropertyNameForGetter("getX"));
		assertEquals("r_d_f", Utils.getPropertyNameForGetter("getRDF"));
		assertEquals("get", Utils.getPropertyNameForGetter("get"));
		assertEquals("invalid_name", Utils.getPropertyNameForGetter("invalidName"));
		assertEquals("a_bunny", Utils.getPropertyNameForGetter("getABunny"));
		assertEquals("your_groove_on", Utils.getPropertyNameForGetter("getYourGrooveOn"));
		assertEquals("xa_y", Utils.getPropertyNameForGetter("getXaY"));
		assertEquals("my_x_m_l_document", Utils.getPropertyNameForGetter("getMyXMLDocument"));

	}
}