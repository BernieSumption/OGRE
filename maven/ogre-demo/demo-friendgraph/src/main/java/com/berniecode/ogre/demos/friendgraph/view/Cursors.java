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

package com.berniecode.ogre.demos.friendgraph.view;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import com.berniecode.ogre.demos.friendgraph.Utils;

public class Cursors {

	public static final Cursor MOVE_CURSOR;
	public static final Cursor NEW_CURSOR;
	public static final Cursor CLICK_CURSOR;

	static {
		MOVE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(Utils.loadImageFromClasspath("move-cursor.gif"),
				new Point(8, 8), "move cursor");
		NEW_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(Utils.loadImageFromClasspath("new-cursor.gif"),
				new Point(1, 1), "new cursor");
		CLICK_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}

}
