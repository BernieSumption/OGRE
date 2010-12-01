package com.berniecode.ogre.engine.shared;

/**
 * A wrapper aroud another OrderedCollection that prevents it from being modified
 * 
 * @author Bernie Sumption
 */
public class ImmutableOrderedCollection implements OrderedCollection {

	private final OrderedCollection child;

	public ImmutableOrderedCollection(OrderedCollection child) {
		this.child = child;
	}

	public Object get(int index) {
		return child.get(index);
	}

	public int indexOf(Object item) {
		return child.indexOf(item);
	}

	public void push(Object item) {
		child.push(item);
	}

	public int size() {
		return child.size();
	}

}
