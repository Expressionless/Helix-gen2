package helix.exception.room;

import helix.Constants;
import helix.exception.HelixException;


// TODO: Document this
public class NoDefaultRoomException extends HelixException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -970484385682052783L;

	private NoDefaultRoomException(String errMessage, Throwable cause) {
		super(Constants.ERR_NO_ROOM, errMessage, cause);
		// TODO Auto-generated constructor stub
	}

	public NoDefaultRoomException(Throwable cause) {
		this("No Default Room Supplied!", cause);
	}
	
	public NoDefaultRoomException() {
		super(Constants.ERR_NO_ROOM, "No Default Room Supplied!");
	}
}
