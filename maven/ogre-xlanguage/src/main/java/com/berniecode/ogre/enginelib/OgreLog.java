/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.StdErrLogWriter;

/**
 * 
 * @author bernie
 */
public class OgreLog {
	
	private OgreLog() {}

	public static final int LEVEL_NONE = 10;
	public static final int LEVEL_ERROR = 5;
	public static final int LEVEL_WARN = 4;
	public static final int LEVEL_INFO = 3;
	public static final int LEVEL_DEBUG = 2;


	private static int currentLevel = LEVEL_INFO;

	private static LogWriter writer = new StdErrLogWriter();
	

	public static void setLevel(int newLevel) {
		currentLevel = newLevel;
	}

	public static void error(String message) {
		doLog(LEVEL_ERROR, "ERROR", message);
	}

	public static void warn(String message) {
		doLog(LEVEL_WARN, "WARN", message);
	}

	public static void info(String message) {
		doLog(LEVEL_INFO, "INFO", message);
	}

	public static void debug(String message) {
		doLog(LEVEL_DEBUG, "DEBUG", message);
	}
	
	public static boolean isDebugEnabled() {
		return isEnabled(LEVEL_DEBUG);
	}
	
	public static boolean isInfoEnabled() {
		return isEnabled(LEVEL_INFO);
	}
	
	public static boolean isWarnEnabled() {
		return isEnabled(LEVEL_WARN);
	}
	
	public static boolean isErrorEnabled() {
		return isEnabled(LEVEL_ERROR);
	}
	


	/**
	 * Set a LogWriter to handle log messages. This can be used to redirect OGRE log messages to any
	 * logging engine. The default behaviour is to print log messages to the standard error stream.
	 */
	public static void setLogWriter(LogWriter writer) {
		if (writer == null) {
			throw new NullPointerException("OgreLogTest.setLogWriter() called with null argument.");
		}
		OgreLog.writer = writer;
	}

	private static void doLog(int level, String levelDescription, String message) {
		if (OgreLog.currentLevel <= level) {
			writer.acceptMessage(level, levelDescription, message);
		}
	}

	private static boolean isEnabled(int level) {
		return currentLevel <= level;
	}

}
