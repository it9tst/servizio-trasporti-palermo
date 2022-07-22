package com.stp.utente.util;

public class ConnectionException extends Exception{
	private static final long serialVersionUID = 1L;
	private int code;
	
	public ConnectionException(String errore){
		super(errore);
	}
	public ConnectionException setErrorCode(int code) {
		this.code=code;
		return this;
	}
	public int getErrorCode() {
		return this.code;
	}
}