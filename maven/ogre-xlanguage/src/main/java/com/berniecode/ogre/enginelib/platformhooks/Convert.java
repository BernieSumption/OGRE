package com.berniecode.ogre.enginelib.platformhooks;

/**
 * Convert between types
 *
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class Convert {
	
	private Convert() {}
	
	public static Object longToObject(long in) {
		return Long.valueOf(in);
	}
}
