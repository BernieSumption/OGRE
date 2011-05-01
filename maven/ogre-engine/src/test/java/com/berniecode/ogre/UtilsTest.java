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