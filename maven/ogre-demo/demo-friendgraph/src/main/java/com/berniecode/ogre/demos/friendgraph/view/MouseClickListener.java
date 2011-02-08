package com.berniecode.ogre.demos.friendgraph.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Convenience class that implements all methods of {@link MouseListener} except
 * {@link #mouseClicked(MouseEvent)}, allowing that to be added in an anonymous class
 * 
 * @author Bernie Sumption
 */
public abstract class MouseClickListener implements MouseListener {
	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

}
