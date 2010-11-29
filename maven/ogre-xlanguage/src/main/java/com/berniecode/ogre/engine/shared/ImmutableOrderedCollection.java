package com.berniecode.ogre.engine.shared;

/**
 * A wrapper aroud another OrderedCollection that prevents it from being modified
 * 
 * @author Bernie Sumption
 */
public class ImmutableOrderedCollection<T> implements OrderedCollection<T> {

	private final OrderedCollection<T> child;

	public ImmutableOrderedCollection(OrderedCollection<T> child) {
		this.child = child;
	}

	@Override
	public T get(int index) {
		return child.get(index);
	}

	@Override
	public int indexOf(T item) {
		return child.indexOf(item);
	}

	@Override
	public void push(T item) {
		child.push(item);
	}

	@Override
	public int size() {
		return child.size();
	}

}
