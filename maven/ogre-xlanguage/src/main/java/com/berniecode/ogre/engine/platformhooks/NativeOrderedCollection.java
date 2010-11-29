package com.berniecode.ogre.engine.platformhooks;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.engine.shared.OrderedCollection;

/**
 * A Java platform implementation of the {@link OrderedCollection} interface
 * 
 * @author Bernie Sumption
 * 
 * @jtoxNative
 */
public class NativeOrderedCollection<T> implements OrderedCollection<T> {

	private List<T> list = new ArrayList<T>();

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(T item) {
		return list.indexOf(item);
	}

	@Override
	public void push(T item) {
		list.add(item);
	}

	@Override
	public int size() {
		return list.size();
	}

}
