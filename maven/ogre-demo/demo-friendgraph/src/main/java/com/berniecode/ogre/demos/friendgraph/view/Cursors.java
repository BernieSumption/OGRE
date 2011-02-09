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
		MOVE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(Utils.loadImageFromClasspath("move-cursor.gif"), new Point(8, 8), "move cursor");
		NEW_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(Utils.loadImageFromClasspath("new-cursor.gif"), new Point(1, 1), "new cursor");
		CLICK_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	}

}
