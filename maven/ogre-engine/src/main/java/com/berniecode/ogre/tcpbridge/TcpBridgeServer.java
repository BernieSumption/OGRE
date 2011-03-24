package com.berniecode.ogre.tcpbridge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.berniecode.ogre.InitialisingBean;
import com.berniecode.ogre.enginelib.OgreLog;
import com.berniecode.ogre.server.ServerEngine;

/**
 * A TCP socket serverEngine that exposes type domains and object graphs over a TCP connection.
 * 
 * <p>
 * The protocol is super simple. The server listens for connections on a configurable TCP port. When
 * a client connects, the serverEngine reads a single line of input from the client then sends a
 * response depending on the request line:
 * 
 * <ul>
 * <li>If the line is "loadTypeDomain:typeDomainId" then the server sends the requested
 * TypeDomainMessage then closes the connection.
 * <li>If the line is "loadObjectGraph:typeDomainId:objectGraphId" then the server sends the
 * requested GraphUpdateMessage then closes the connection.
 * <li>If the line is "subscribeToGraphUpdates:typeDomainId:objectGraphId" then the server enters
 * into a loop, sending a GraphUpdateMessage for each change to the object graph, as the changes
 * occur. This loop continues until the client disconnects or the server exits.
 * </ul>
 * 
 * <p>
 * Responses are serialised in the OGRE Wire Format.
 * 
 * @author Bernie Sumption
 */
public class TcpBridgeServer extends InitialisingBean {

	private ServerEngine serverEngine;
	private Integer port;
	private ServerThread serverThread;

	/**
	 * Set the TCP port to listen for new connections on
	 */
	public void setPort(int port) {
		requireInitialised(false, "setPort()");
		this.port = port;
	}

	/**
	 * Set the server engine to be exposed through this download adapter
	 */
	public void setServerEngine(ServerEngine server) {
		requireInitialised(false, "setServerEngine()");
		this.serverEngine = server;
	}

	@Override
	protected void doInitialise() {
		requireNotNull(serverEngine, "serverEngine");
		requireNotNull(port, "port");
		serverThread = new ServerThread();
		serverThread.start();
	}

	private class ServerThread extends Thread {

		public ServerThread() {
			setName("TcpBridgeServer:" + port);
		}

		private ServerSocket socket;

		@Override
		public void run() {
			try {
				socket = new ServerSocket(port);
				while (true) {
					try {
						doConversation();
					} catch (IOException e) {
						OgreLog.error("Conversation closed due to exception: " + e.getMessage());
						return;
					}
				}
			} catch (Exception e) {
				OgreLog.error("Error starting socket server: " + e.getMessage());
			}
		}

		private void doConversation() throws IOException {
			Socket connectionSocket = null;
			try {
				connectionSocket = socket.accept();
			} catch (SocketException e) {
				return; // not an error - quit() called while waiting for a new connection
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String request = in.readLine();
			System.out.println("Received: " + request);
			String response = request.toUpperCase() + '\n';
			DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
			out.writeBytes(response);
			socket.close();
		}

		/**
		 * Stop accepting new connections, and close all currently open connections
		 */
		public void quit() {
			try {
				socket.close();
				try {
					join();
				} catch (InterruptedException e) {
				}
			} catch (IOException e) {
				OgreLog.error("IOException while closing server socket: " + e.getMessage());
			}
		}
	}

	/**
	 * Stop the server. This method blocks until the network connections used by the server have
	 * been closed
	 */
	public void quit() {
		serverThread.quit();
	}

}