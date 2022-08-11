package io.sly.helix.exception;

import org.jboss.logging.Logger;

public class HelixException extends Exception implements HelixExceptionIntf {
	private static final Logger logger = Logger.getLogger(HelixException.class);
	
	public final Integer statusCode;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4451354660120864139L;

	public HelixException(Integer statusCode, Throwable cause) {
		super(cause);
		this.statusCode = statusCode;
	}
	
	public HelixException(Integer statusCode, String errMessage) {
		super(errMessage);
		this.statusCode = statusCode;
	}
	
	public HelixException(Integer statusCode, String errMessage, Throwable cause) {
		super("HELIX EXCEPTION: " + errMessage, cause);
		this.statusCode = statusCode;
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public Throwable getThrowable() {
		return this;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
