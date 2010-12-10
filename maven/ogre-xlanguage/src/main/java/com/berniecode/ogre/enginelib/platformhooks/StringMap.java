package com.berniecode.ogre.enginelib.platformhooks;

import java.util.HashMap;
import java.util.Map;

/**
 * A map with String keys and Object values
 * 
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class StringMap {

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

	public Object[] getValues() {
		return map.values().toArray();
	}

}
