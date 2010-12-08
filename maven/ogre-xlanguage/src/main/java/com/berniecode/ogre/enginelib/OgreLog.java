package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.StdErrLogWriter;

/**
 * 
 * @author bernie
 * 
 * @jtoxNative - not translated into other languages
 */
public class OgreLog {
	
	private OgreLog() {}

	public static final int NONE = 10;
	public static final int ERROR = 5;
	public static final int WARN = 4;
	public static final int INFO = 3;
	public static final int DEBUG = 2;


	private static int currentLevel = DEBUG;

	private static LogWriter writer = new StdErrLogWriter();
	

	public static void setLevel(int newLevel) {
		currentLevel = newLevel;
	}

	public static void error(String message) {
		doLog(ERROR, "ERROR", message);
	}

	public static void warn(String message) {
		doLog(WARN, "WARN", message);
	}

	public static void info(String message) {
		doLog(INFO, "INFO", message);
	}

	public static void debug(String message) {
		doLog(DEBUG, "DEBUG", message);
	}
	
	public static boolean isDebugEnabled() {
		return isEnabled(DEBUG);
	}
	
	public static boolean isInfoEnabled() {
		return isEnabled(INFO);
	}
	
	public static boolean isWarnEnabled() {
		return isEnabled(WARN);
	}
	
	public static boolean isErrorEnabled() {
		return isEnabled(ERROR);
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
