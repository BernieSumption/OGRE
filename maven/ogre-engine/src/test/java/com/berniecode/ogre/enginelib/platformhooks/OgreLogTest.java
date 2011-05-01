package com.berniecode.ogre.enginelib.platformhooks;

import junit.framework.TestCase;

import com.berniecode.ogre.enginelib.LogWriter;
import com.berniecode.ogre.enginelib.OgreLog;

/**
 * 
 * @author bernie
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
		OgreLog.setLevel(OgreLog.LEVEL_INFO);
		OgreLog.debug("a");
		assertNoLog();

		reset();
		OgreLog.setLevel(OgreLog.LEVEL_DEBUG);
		OgreLog.debug("a");
		assertTrue(OgreLog.isDebugEnabled());
		assertEquals(OgreLog.LEVEL_DEBUG, lastLogLevel);
		assertEquals("DEBUG", lastLogLevelDesc);
		assertEquals("a", lastLogMessage);

		reset();
		OgreLog.setLevel(OgreLog.LEVEL_WARN);
		OgreLog.info("b");
		assertNoLog();
		
		reset();
		OgreLog.setLevel(OgreLog.LEVEL_INFO);
		OgreLog.info("b");
		assertFalse(OgreLog.isDebugEnabled());
		assertTrue(OgreLog.isInfoEnabled());
		assertEquals(OgreLog.LEVEL_INFO, lastLogLevel);
		assertEquals("INFO", lastLogLevelDesc);
		assertEquals("b", lastLogMessage);

		reset();
		OgreLog.setLevel(OgreLog.LEVEL_ERROR);
		OgreLog.info("c");
		assertNoLog();

		reset();
		OgreLog.setLevel(OgreLog.LEVEL_WARN);
		OgreLog.warn("c");
		assertFalse(OgreLog.isInfoEnabled());
		assertTrue(OgreLog.isWarnEnabled());
		assertEquals(OgreLog.LEVEL_WARN, lastLogLevel);
		assertEquals("WARN", lastLogLevelDesc);
		assertEquals("c", lastLogMessage);

		reset();
		OgreLog.setLevel(OgreLog.LEVEL_NONE);
		OgreLog.info("d");
		assertFalse(OgreLog.isErrorEnabled());
		assertNoLog();

		reset();
		OgreLog.setLevel(OgreLog.LEVEL_ERROR);
		OgreLog.error("d");
		assertFalse(OgreLog.isWarnEnabled());
		assertTrue(OgreLog.isErrorEnabled());
		assertEquals(OgreLog.LEVEL_ERROR, lastLogLevel);
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
