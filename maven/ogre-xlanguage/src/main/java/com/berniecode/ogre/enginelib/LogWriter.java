package com.berniecode.ogre.enginelib;

/**
 * Connects OGRE to a platform-specific logging system
 *
 * @author Bernie Sumption
 */
public interface LogWriter {
	public void acceptMessage(int level, String levelDescription, String message);
}