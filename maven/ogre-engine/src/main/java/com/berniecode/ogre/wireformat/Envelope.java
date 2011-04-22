package com.berniecode.ogre.wireformat;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * Contains methods to wrap and unwrap binary messages in a header that specifies the length of the
 * message, so that they can be transmitted over network streams.
 * 
 * @author Bernie Sumption
 */
public class Envelope {

	private static final byte[] ENVELOPE_HEADER = new byte[] { 'O', 'G', 'R', 'E', 'v', '1' };

	/**
	 * Read a binary message wrapped in an OGRE envelope
	 * 
	 * @return the binary message, after discarding the envelope header
	 */
	public static byte[] readEnvelopedBytes(InputStream inputStream) throws IOException {
		byte[] cbuf = new byte[ENVELOPE_HEADER.length];
		int numRead = inputStream.read(cbuf);
		if (numRead < ENVELOPE_HEADER.length) {
			throw new IOException("End of stream reached before envelope header could be read.");
		}
		if (!Arrays.equals(cbuf, ENVELOPE_HEADER)) {
			throw new OgreException("Invalid OGRE envelope header. Expected " + Arrays.toString(ENVELOPE_HEADER)
					+ " (OGREv1), got " + Arrays.toString(cbuf) + " (" + new String(cbuf) + ")");
		}
		int length = new DataInputStream(inputStream).readInt();
		byte[] message = new byte[length];
		int bytesRead = inputStream.read(message);
		if (bytesRead != length) {
			throw new IOException("End of stream reached before the number of bytes promised in the envelope header ("
					+ length + ") could be read.");
		}
		return message;
	}

	public static byte[] wrapInEnvelope(byte[] payload) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(payload.length + ENVELOPE_HEADER.length + 4);
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.write(ENVELOPE_HEADER);
			dos.writeInt(payload.length);
			dos.write(payload);
		} catch (IOException e) {
			throw new RuntimeException("ByteArrayOutputStream threw IOException - should never happen!");
		}
		return bos.toByteArray();
	}

}
