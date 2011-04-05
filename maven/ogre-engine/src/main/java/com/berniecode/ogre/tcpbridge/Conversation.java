package com.berniecode.ogre.tcpbridge;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a conversation with a client.
 * 
 * @author Bernie Sumption
 */
class Conversation {

	public static enum Type {
		LOAD_TYPE_DOMAIN, LOAD_OBJECT_GRAPH, SUBSCRIBE_TO_GRAPH_UPDATES,
	}

	private final SelectionKey key;

	private StringBuilder requestBuilder = new StringBuilder();
	private boolean requestIsComplete = false;
	private String error;
	private Type type;
	private String typeDomainId;
	
	private Queue<ByteBuffer> dataToSend = new LinkedList<ByteBuffer>();

	public Conversation(SelectionKey key) {
		this.key = key;
	}

	/**
	 * Accept data that has been sent from the client. If the data completes the request, return
	 * true. Thereafter, the parsed request data will be available as
	 * 
	 * @return true if this data has completed the requestBuilder, false otherwise
	 */
	public boolean acceptData(String data) {
		if (requestIsComplete) {
			return false;
		}
		requestBuilder.append(data);
		if (data.indexOf('\n') == -1) {
			return false;
		}
		parseRequest();
		requestIsComplete = true;
		key.interestOps();
		return true;
	}

	/**
	 * Check whether this request resulted in an error. If true, the error message text is available
	 * through {@link #getError()}
	 */
	public boolean isError() {
		return error != null;
	}

	/**
	 * Return the error message text, or null if there was no error
	 */
	public String getError() {
		return error;
	}

	/**
	 * Return the type of this request, or null if there has been an error
	 */
	public Type getType() {
		return type;
	}


	/**
	 * Return the type domain associated with this request
	 */
	public String getTypeDomainId() {
		return typeDomainId;
	}

	/**
	 * Check whether this conversation should be closed when the remaining data has finished writing
	 */
	public boolean isCloseOnComplete() {
		return type != Type.SUBSCRIBE_TO_GRAPH_UPDATES;
	}

	/**
	 * Send some bytes to the client then close the connection
	 */
	public void addDataToSend(byte[] response) {
		synchronized (dataToSend) {
			dataToSend.add(ByteBuffer.wrap(response));
		}
	}

	/**
	 * Return a {@link ByteBuffer} of data to send, or null if there is no data remaining
	 */
	public ByteBuffer getNextDataToSend() {
		synchronized (dataToSend) {
			while(true) {
				if (dataToSend.size() == 0) {
					return null;
				}
				if (dataToSend.peek().hasRemaining()) {
					return dataToSend.peek();
				}
				dataToSend.poll();
			}
		}
	}
	
	//
	// INTERNAL MACHINERY
	//

	private void parseRequest() {
		// every code path through this method must either set this.error, or correctly populate this.type, this.typeDomainId and this.objectGraphId
		String req = requestBuilder.toString();
		req = req.substring(0, req.indexOf('\n')).trim();
		String[] parts = req.split("\t");
		if (parts[0].equals("loadTypeDomain")) {
			if (parts.length != 2) {
				error = "Malformed request: too many arguments to loadTypeDomain";
				return;
			}
			type = Type.LOAD_TYPE_DOMAIN;
			typeDomainId = parts[1];
		} else {
			error = "Unrecognised command '" + parts[0] + "'";
		}
	}

}
