/*
 * Copyright 2011 Bernie Sumption. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. THIS SOFTWARE IS PROVIDED ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * FREEBSD PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

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
