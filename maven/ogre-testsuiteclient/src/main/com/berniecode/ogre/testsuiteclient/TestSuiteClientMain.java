package com.berniecode.ogre.testsuiteclient;

import java.io.File;
import java.io.IOException;

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
		File typeDomainMessage = new File(suiteFolder, TestSuiteClient.TYPE_DOMAIN_MESSAGE_FILE_NAME);
		File initialDataMessage = new File(suiteFolder, TestSuiteClient.INITIAL_DATA_MESSAGE_FILE_NAME);
		if (!typeDomainMessage.isFile()) {
			System.err.println("There is no type domain message at " + typeDomainMessage.getPath());
			System.exit(1);
		}
		if (!typeDomainMessage.isFile()) {
			System.err.println("There is no initial data message at " + initialDataMessage.getPath());
			System.exit(1);
		}
		
		new TestSuiteClient(suiteFolder).runTestSuite();
		
	}

}
