package com.sh.atlas.app.exceptions;

public class BackupExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7722860344820809842L;
	
	public BackupExistsException() {
		super();
	}
	
	public BackupExistsException(String message,Throwable cause) {
		super(message,cause);
	}
	
	public BackupExistsException(String message) {
		super(message);
	}
	
}
