package com.berniecode.ogre.enginelib.platformhooks;

import com.berniecode.ogre.enginelib.LogWriter;

/**
 * A {@link LogWriter} that prints messages on the standard error stream
 *
 * @author Bernie Sumption
 */
public class StdErrLogWriter implements LogWriter {
	public void acceptMessage(int level, String levelDescription, String message) {
		System.err.println("[OGRE] " + levelDescription + " - " + message);
	}
}