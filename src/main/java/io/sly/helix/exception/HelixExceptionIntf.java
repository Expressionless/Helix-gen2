package io.sly.helix.exception;

import org.jboss.logging.Logger;

import com.badlogic.gdx.Gdx;

import io.sly.helix.game.BaseGame;

public interface HelixExceptionIntf {
	
    public Throwable getThrowable();
	
    public int getStatusCode();
    public String getMessage();

    public Logger getLogger();

	public default void printException() {
		StackTraceElement[] stack = getThrowable().getStackTrace();
		for(StackTraceElement traceElement : stack) {
			if(traceElement.getClassName().startsWith("com.badlogic.gdx"))
				continue;
			String line = "line:" + Integer.toString(traceElement.getLineNumber());
			line += "@" + traceElement.getClassName();
			line += ":" + traceElement.getMethodName();
			System.err.println(line);
		}
	}

	public default void terminateGame(BaseGame game) {
		getLogger().error(this.getMessage());
		game.dispose();
		getLogger().error("Exiting with code: " + this.getStatusCode());
		Gdx.app.exit();
	}
}
