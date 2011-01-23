package com.berniecode.ogre.testsuiteclient;

import java.io.File;
import java.io.IOException;

import com.berniecode.ogre.enginelib.OgreLog;

public class TestSuiteClientMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("This program expects a test suite folder as a single argument");
			System.exit(1);
		}
		File suiteFolder = new File(args[0]);
		if (!suiteFolder.isDirectory()) {
			System.err.println("The path " + suiteFolder.getPath() + " is not a directory");
			System.exit(1);
		}
		OgreLog.setLevel(OgreLog.LEVEL_WARN);
		for (File childFolder: suiteFolder.listFiles()) {
			if (new File(childFolder, TestSuiteClient.TYPE_DOMAIN_MESSAGE_FILE_NAME).exists()) {
				System.err.println("Running test suite " + childFolder);
				new TestSuiteClient(childFolder).runTestSuite();
			}
		}
		
		
	}

}
