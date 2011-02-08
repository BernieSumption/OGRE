package com.berniecode.ogre.demos.friendgraph;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Utils {

	/**
	 * Display a UI to choose a JPEG file and return it as a byte array.
	 * 
	 * @return a byte array, or null if the user cancelled the operation
	 * @throws IOException if a file was selected that could not be loaded
	 */
	public static byte[] loadImageBytesFromFile() throws IOException {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG Images", "jpg");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			byte[] result = new byte[(int) selected.length()];
			FileInputStream is = null;
			try {
				is = new FileInputStream(selected);
				is.read(result);
			} finally {
				if (is != null) {
					is.close();
				}
			}
			return result;
		}
		return null;
	}

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
