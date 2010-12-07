package com.berniecode.ogre.enginelib.platformhooks;

import java.util.ArrayList;
import java.util.List;

import com.berniecode.ogre.enginelib.shared.SimpleList;

/**
 * An implementation of {@link SimpleList} backed by a {@link java.util.ArrayList}
 *
 * @author Bernie Sumption
 */
public class NativeSimpleList implements SimpleList {
	
	private List list = new ArrayList();

	public void add(Object object) {
		list.add(object);
	}

	public Object get(int index) {
		return list.get(index);
	}

	public int size() {
		return list.size();
	}

	public void addAll(Object[] objects) {
		for (int i=0; i<objects.length; i++) {
			add(objects[i]);
		}
	}

	public void copyToArray(Object[] destination) {
		for (int i=0; i<list.size(); i++) {
			destination[i] = list.get(i);
		}
	}

}
