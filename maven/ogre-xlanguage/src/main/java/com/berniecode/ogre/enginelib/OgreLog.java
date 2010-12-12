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

	public static final int LEVEL_NONE = 10;
	public static final int LEVEL_ERROR = 5;
	public static final int LEVEL_WARN = 4;
	public static final int LEVEL_INFO = 3;
	public static final int LEVEL_DEBUG = 2;


	private static int currentLevel = LEVEL_DEBUG;

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
