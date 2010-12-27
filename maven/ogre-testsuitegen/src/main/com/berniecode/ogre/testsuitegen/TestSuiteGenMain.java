package com.berniecode.ogre.testsuitegen;

import java.io.File;

public class TestSuiteGenMain {
	
	public static void main(String[] args) {
		if (args.length == 1 && "--help".equals(args[0])) {
			System.err.println("Generates a number of test suites in a specified directory.");
			printHelpAndExit();
		}
		if (args.length != 3) {
			System.err.println("Incorrect number of arguments - 3 expected, " + args.length + " received");
			printHelpAndExit();
		}
		try {
			int from = Integer.parseInt(args[0]), to = Integer.parseInt(args[1]);
			File outputFolder = new File(args[2]);
			if (!outputFolder.isDirectory()) {
				System.err.println(args[2] + " is not a folder");
				printHelpAndExit();
			}
			new TestSuiteGenerator().generateTestSuites(from, to, outputFolder);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			printHelpAndExit();
		}
	}

	private static void printHelpAndExit() {
		System.err.println("Usage: java -jar testsuitegen.jar from to outputfolder");
		System.err.println("    or java -jar testsuitegen.jar --help");
		System.exit(1);
	}

}
