package com.berniecode.ogre.enginelib.platformhooks;

/**
 * Convert between types
 *
 * @author Bernie Sumption
 */
public class Convert {
	
	private Convert() {}
	
	public static Object longToObject(long in) {
		return Long.valueOf(in);
	}
}
