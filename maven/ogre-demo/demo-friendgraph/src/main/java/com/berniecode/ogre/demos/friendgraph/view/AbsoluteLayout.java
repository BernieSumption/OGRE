package com.berniecode.ogre.demos.friendgraph.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

class AbsoluteLayout implements LayoutManager {
	public void addLayoutComponent(String name, Component comp) { }

	public void removeLayoutComponent(Component comp) { }

	public Dimension preferredLayoutSize( Container container ) {
		return new Dimension( 0, 0 );
	}

	public Dimension minimumLayoutSize(Container cont) {
		return preferredLayoutSize(cont);
	}

	public void layoutContainer(Container container) {
		for (Component c: container.getComponents()) {
			c.setBounds(c.getX(), c.getY(), c.getWidth(), c.getHeight());
		}
	}
}