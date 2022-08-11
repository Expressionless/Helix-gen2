package io.sly.helix.exception.screen;

import io.sly.helix.Constants;
import io.sly.helix.exception.HelixRuntimeException;


// TODO: Document this
public class NoDefaultScreenException extends HelixRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -970484385682052783L;

	private NoDefaultScreenException(String errMessage, Throwable cause) {
		super(Constants.ERR_NO_ROOM, errMessage, cause);
	}

	public NoDefaultScreenException(Throwable cause) {
		this("No Default Screen Supplied!", cause);
	}
	
	public NoDefaultScreenException() {
		super(Constants.ERR_NO_ROOM, "No Default Screen Supplied!");
	}
}
