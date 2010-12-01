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
public class NativeStringMap implements StringMap {

	Map map = new HashMap();

	public boolean contains(String key) {
		return map.containsKey(key);
	}

	public Object get(String key) {
		return map.get(key);
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

}
