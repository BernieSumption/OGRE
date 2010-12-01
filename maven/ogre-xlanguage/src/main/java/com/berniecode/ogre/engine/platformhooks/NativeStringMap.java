package com.berniecode.ogre.engine.platformhooks;

import java.util.HashMap;
import java.util.Map;

import com.berniecode.ogre.engine.shared.StringMap;

/**
 * A Java platform implementation of the {@link StringMap} interface backed by a Java collections
 * {@link Map}
 * 
 * @author Bernie Sumption
 */
public class NativeStringMap<T> implements StringMap<T> {

	Map<String, T> map = new HashMap<String, T>();

	@Override
	public boolean contains(String key) {
		return map.containsKey(key);
	}

	@Override
	public T get(String key) {
		return map.get(key);
	}

	@Override
	public void put(String key, T value) {
		map.put(key, value);
	}

}
