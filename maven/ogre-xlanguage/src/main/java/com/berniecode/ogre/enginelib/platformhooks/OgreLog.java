package com.berniecode.ogre.enginelib.platformhooks;

/**
 * 
 * @author bernie
 * 
 * @jtoxNative - not translated into other languages
 */
public class OgreLog {

	//
	// PUBLIC API - MUST BE RE-IMPLEMENTED IN EVERY HOST LANGUAGE
	//

	public static final int ERROR = 5;
	public static final int WARN = 4;
	public static final int INFO = 3;
	public static final int DEBUG = 2;

	public static void setLevel(int newLevel) {
		level = newLevel;
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

	//
	// PRIVATE API - JAVA ONLY
	//

	public static interface LogWriter {
		public void acceptMessage(int level, String levelDescription, String message);
	}

	/**
	 * Set a LogWriter to handle log messages. This can be used to redirect OGRE log messages to any
	 * logging engine. The default behaviour is to print log messages to the standard error stream.
	 */
	public static void setLogWriter(LogWriter writer) {
		if (writer == null) {
			throw new NullPointerException("OgreLog.setLogWriter() called with null argument.");
		}
		OgreLog.writer = writer;
	}

	private static int level = DEBUG;

	private static LogWriter writer = new StdErrLogWriter();

	private static void doLog(int level, String levelDescription, String message) {
		if (OgreLog.level <= level) {
			writer.acceptMessage(level, levelDescription, message);
		}
	}

	private static class StdErrLogWriter implements LogWriter {
		public void acceptMessage(int level, String levelDescription, String message) {
			System.err.println("[OGRE] " + levelDescription + " - " + message);
		}
	}
}
