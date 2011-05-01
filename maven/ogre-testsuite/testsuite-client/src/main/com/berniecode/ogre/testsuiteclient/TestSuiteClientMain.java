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
