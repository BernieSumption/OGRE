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
