package com.berniecode.ogre.engine.platformhooks;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.engine.shared.OrderedCollection;

/**
 * A Java platform implementation of the {@link OrderedCollection} interface
 * 
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class NativeOrderedCollection implements OrderedCollection {

	private List list = new ArrayList();

	public Object get(int index) {
		return list.get(index);
	}

	public int indexOf(Object item) {
		return list.indexOf(item);
	}

	public void push(Object item) {
		list.add(item);
	}

	public int size() {
		return list.size();
	}

}
