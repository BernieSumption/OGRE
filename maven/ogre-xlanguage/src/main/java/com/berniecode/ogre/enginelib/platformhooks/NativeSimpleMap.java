package com.berniecode.ogre.enginelib.platformhooks;

import java.util.HashMap;
import java.util.Map;

import com.berniecode.ogre.enginelib.shared.SimpleMap;

/**
 * A Java platform implementation of the {@link SimpleMap} interface backed by a Java collections
 * {@link Map}
 * 
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class NativeSimpleMap implements SimpleMap {

	Map map = new HashMap();

	public boolean contains(Object key) {
		return map.containsKey(key);
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public void put(Object key, Object value) {
		if (!(key instanceof String || key instanceof Long || key instanceof Integer)) {
			throw new OgreException("Only Strings and numbers are suitable for map keys, do to" +
					" weirdness with using object keys in other languages.");
		}
		map.put(key, value);
	}

	public Object[] getValues() {
		return map.values().toArray();
	}

}
