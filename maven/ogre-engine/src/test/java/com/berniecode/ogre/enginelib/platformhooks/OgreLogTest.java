package com.berniecode.ogre.enginelib.platformhooks;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.LogWriter;
import com.berniecode.ogre.enginelib.OgreLog;

/**
 * 
 * @author bernie
 * 
 * @jtoxNative - not translated into other languages
 */
public class OgreLogTest extends TestCase {

	private String lastLogMessage = null;
	private int lastLogLevel = -1;
	private String lastLogLevelDesc = null;

	public void testSetWriterFailsWithNull() {
		try {
			OgreLog.setLogWriter(null);
			fail("setWriter() should fail with a null value");
		} catch (NullPointerException e) {}
		
		OgreLog.setLogWriter(new LogWriter() {
			@Override
			public void acceptMessage(int level, String levelDescription, String message) {
				lastLogLevel = level;
				lastLogLevelDesc = levelDescription;
				lastLogMessage = message;
			}
		});

		reset();
		OgreLog.setLevel(OgreLog.INFO);
		OgreLog.debug("a");
		assertNoLog();

		reset();
		OgreLog.setLevel(OgreLog.DEBUG);
		OgreLog.debug("a");
		assertEquals(OgreLog.DEBUG, lastLogLevel);
		assertEquals("DEBUG", lastLogLevelDesc);
		assertEquals("a", lastLogMessage);

		reset();
		OgreLog.setLevel(OgreLog.WARN);
		OgreLog.info("b");
		assertNoLog();
		
		reset();
		OgreLog.setLevel(OgreLog.INFO);
		OgreLog.info("b");
		assertEquals(OgreLog.INFO, lastLogLevel);
		assertEquals("INFO", lastLogLevelDesc);
		assertEquals("b", lastLogMessage);

		reset();
		OgreLog.setLevel(OgreLog.ERROR);
		OgreLog.info("c");
		assertNoLog();

		reset();
		OgreLog.setLevel(OgreLog.WARN);
		OgreLog.warn("c");
		assertEquals(OgreLog.WARN, lastLogLevel);
		assertEquals("WARN", lastLogLevelDesc);
		assertEquals("c", lastLogMessage);

		reset();
		OgreLog.setLevel(OgreLog.NONE);
		OgreLog.info("d");
		assertNoLog();

		reset();
		OgreLog.setLevel(OgreLog.ERROR);
		OgreLog.error("d");
		assertEquals(OgreLog.ERROR, lastLogLevel);
		assertEquals("ERROR", lastLogLevelDesc);
		assertEquals("d", lastLogMessage);
	}
	
	private void reset() {
		lastLogLevel = -1;
		lastLogLevelDesc = null;
		lastLogMessage = null;
	}
	
	private void assertNoLog() {
		assertEquals(-1, lastLogLevel);
		assertEquals(null, lastLogLevelDesc);
		assertEquals(null, lastLogMessage);
	}
}
