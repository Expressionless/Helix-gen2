package helix.exception;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;

import helix.game.BaseGame;

public class HelixException extends Exception {
	private static final Logger logger = Logger.getLogger(HelixException.class.getCanonicalName());
	
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
	
	public void printException() {
		StackTraceElement[] stack = this.getStackTrace();
		for(StackTraceElement traceElement : stack) {
			if(traceElement.getClassName().startsWith("com.badlogic.gdx"))
				continue;
			String line = "line:" + Integer.toString(traceElement.getLineNumber());
			line += "@" + traceElement.getClassName();
			line += ":" + traceElement.getMethodName();
			System.err.println(line);
		}
	}
	
	public void terminateGame(BaseGame game) {
		game.dispose();
		System.err.println("Exiting with code: " + this.statusCode);
		Gdx.app.exit();
	}
}
