package helix.exception.res;

import helix.Constants;

public class ResourceNotFoundException extends ResourceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7784310857403892163L;

	public ResourceNotFoundException(String path) {
		super(Constants.ERR_RES_NOT_FOUND, path, null);
		System.err.println("Resource not found at path: " + path);
	}

}
