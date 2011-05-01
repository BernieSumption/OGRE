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

package com.berniecode.ogre.demos.friendgraph;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Utils {

	/**
	 * Load a file as a byte array.
	 */
	public static byte[] loadBytesFromFile(File file) throws IOException {
		byte[] result = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(result);
		fis.close();
		return result;
	}
	
	public static Image loadImageFromFile(String fileName) {
		try {
			return ImageIO.read(new File(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Image loadImageFromBytes(byte[] bytes) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			BufferedImage image = ImageIO.read(is);
			is.close();
			return image;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Image loadImageFromClasspath(String fileName) {
		try {
			InputStream is = Utils.class.getClassLoader().getResourceAsStream(fileName);
			BufferedImage image = ImageIO.read(is);
			is.close();
			return image;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
