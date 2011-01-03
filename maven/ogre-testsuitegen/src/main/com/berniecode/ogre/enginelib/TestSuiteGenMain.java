package com.berniecode.ogre.enginelib;

import java.io.File;
import java.io.IOException;

public class TestSuiteGenMain {
	
	public static void main(String[] args) throws IOException {
		if (args.length == 1 && "--help".equals(args[0])) {
			System.err.println("Generates a number of test suites in a specified directory.");
			printHelpAndExit();
		}
		if (args.length != 4) {
			System.err.println("Incorrect number of arguments - 3 expected, " + args.length + " received");
			printHelpAndExit();
		}
		int from = 0, to = 0, iterations = 0;
		try {
			from = Integer.parseInt(args[0]);
			to = Integer.parseInt(args[1]);
			iterations = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			System.err.println("Error: " + e);
			printHelpAndExit();
		}
		
		File outputFolder = new File(args[3]);
		if (!outputFolder.isDirectory()) {
			if (!outputFolder.mkdirs()) {
				System.err.println("Attempt to create folder " + args[3] + " failed.");
				printHelpAndExit();
			}
		}
		System.err.println("Generating suites " + from + " to " + to + " with " + iterations + " iterations");
		for (int i = from; i <= to; i++) {
			System.err.println("Generating suite " + i);
			new TestSuiteGenerator(i, iterations, new File(outputFolder, String.valueOf(i))).generateTestSuite();
		}
		System.err.println("Done!");
	}

	private static void printHelpAndExit() {
		System.err.println("Usage: java -jar testsuitegen.jar from to outputfolder");
		System.err.println("    or java -jar testsuitegen.jar --help");
		System.exit(1);
	}

}
