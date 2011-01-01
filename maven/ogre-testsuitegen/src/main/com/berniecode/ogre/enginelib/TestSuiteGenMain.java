package com.berniecode.ogre.enginelib;

import java.io.File;
import java.io.IOException;

public class TestSuiteGenMain {
	
	public static void main(String[] args) throws IOException {
		if (args.length == 1 && "--help".equals(args[0])) {
			System.err.println("Generates a number of test suites in a specified directory.");
			printHelpAndExit();
		}
		if (args.length != 3) {
			System.err.println("Incorrect number of arguments - 3 expected, " + args.length + " received");
			printHelpAndExit();
		}
		int from = 0, to = 0;
		try {
			from = Integer.parseInt(args[0]);
			to = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.err.println("Error: " + e);
			printHelpAndExit();
		}
		
		File outputFolder = new File(args[2]);
		if (!outputFolder.isDirectory()) {
			if (!outputFolder.mkdirs()) {
				System.err.println("Attempt to create folder " + args[2] + " failed.");
				printHelpAndExit();
			}
		}
		System.err.println("Generating suites " + from + " to " + to);
		for (int i = from; i <= to; i++) {
			new TestSuiteGenerator(i, new File(outputFolder, String.valueOf(i))).generateTestSuite();
			System.err.println("Generating suite " + i);
		}
	}

	private static void printHelpAndExit() {
		System.err.println("Usage: java -jar testsuitegen.jar from to outputfolder");
		System.err.println("    or java -jar testsuitegen.jar --help");
		System.exit(1);
	}

}
