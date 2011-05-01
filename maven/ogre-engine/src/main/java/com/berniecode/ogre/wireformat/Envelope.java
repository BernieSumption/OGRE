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

package com.berniecode.ogre.wireformat;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Contains methods to wrap and unwrap binary messages in a header that specifies the length of the
 * message, so that they can be transmitted over network streams.
 * 
 * @author Bernie Sumption
 */
public class Envelope {

	private static final byte[] ENVELOPE_HEADER = new byte[] { 'O', 'G', 'R', 'E', 'v', '1' };

	public static final byte ERROR_MESSAGE_PAYLOAD = 0x01;
	public static final byte TYPE_DOMAIN_MESSAGE_PAYLOAD = 0x02;
	public static final byte OBJECT_GRAPH_MESSAGE_PAYLOAD = 0x03;

	/**
	 * Read a binary message wrapped in an OGRE envelope
	 * 
	 * @return the binary message, after discarding the envelope header, or null if the end of the
	 *         {@link InputStream} has been reached
	 */
	public static byte[] readEnvelopedBytes(InputStream inputStream) throws IOException {
		byte[] cbuf = new byte[ENVELOPE_HEADER.length];
		int numRead = inputStream.read(cbuf);
		if (numRead == -1) {
			return null;
		}
		if (numRead < ENVELOPE_HEADER.length) {
			throw new IOException("End of stream reached before envelope header could be read.");
		}
		if (!Arrays.equals(cbuf, ENVELOPE_HEADER)) {
			throw new IOException("Invalid OGRE envelope header. Expected " + Arrays.toString(ENVELOPE_HEADER)
					+ " (OGREv1), got " + Arrays.toString(cbuf) + " (" + new String(cbuf) + ")");
		}
		DataInputStream dis = new DataInputStream(inputStream);
		dis.read(); // skip payload type byte
		int length = dis.readInt();
		byte[] message = new byte[length];
		int bytesRead = inputStream.read(message);
		if (bytesRead != length) {
			throw new IOException("End of stream reached before the number of bytes promised in the envelope header ("
					+ length + ") could be read.");
		}
		return message;
	}

	public static byte[] wrapInEnvelope(byte[] payload, byte payloadType) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(payload.length + ENVELOPE_HEADER.length + 4);
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.write(ENVELOPE_HEADER);
			dos.write(payloadType);
			dos.writeInt(payload.length);
			dos.write(payload);
		} catch (IOException e) {
			throw new RuntimeException("ByteArrayOutputStream threw IOException - should never happen!");
		}
		return bos.toByteArray();
	}

}
