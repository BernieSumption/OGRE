package com.berniecode.ogre.client;

public class ClientFacadeException extends RuntimeException {

	public ClientFacadeException(String message) {
		super(message);
	}
	
	public ClientFacadeException(String message, Throwable cause) {
		super(message, cause);
	}

}
