package com.berniecode.ogre.enginelib.platformhooks;

import java.util.HashMap;
import java.util.Map;

/**
 * A map with String keys and Object values
 * 
 * @author Bernie Sumption
 */
public class StringMap {

	Map map = new HashMap();

	public Object get(String key) {
		return map.get(key);
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

}
