package com.berniecode.ogre.enginelib.platformhooks;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility for building typed arrays
 *
 * @author Bernie Sumption
 */
public class ArrayBuilder {
	
	private final Class componentType;
	private List contents = new ArrayList();

	public ArrayBuilder(Class componentType) {
		this.componentType = componentType;
	}
	
	/**
	 * Add a value
	 */
	public void add(Object object) {
		contents.add(componentType.cast(object));
	}

	/**
	 * Add each value in an array of values
	 */
	public void addAll(Object[] objects) {
		for (int i=0; i<objects.length; i++) {
			add(objects[i]);
		}
	}

	/**
	 * Make an array containing all the items added to this builder
	 */
	public Object[] buildArray() {
		Object[] result = (Object[]) Array.newInstance(componentType, contents.size());
		for (int i=0; i<result.length; i++) {
			result[i] = contents.get(i);
		}
		return result;
	}

}
