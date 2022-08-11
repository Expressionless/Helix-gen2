package io.sly.helix.exception.res;

import io.sly.helix.Constants;
import io.sly.helix.exception.HelixException;

public abstract class ResourceException extends HelixException {

	public final String resourcePath;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711168572058227274L;

	public ResourceException(Integer statusCode, String path, Throwable cause) {
		super((statusCode != null ? statusCode : Constants.ERR_RES_LOAD_FAIL), "Problem loading resource: " + path, cause);
		resourcePath = path;
	}

}
