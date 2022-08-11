package io.sly.helix;


import java.io.File;

import com.badlogic.gdx.graphics.Color;

/**
 * Engine constants
 * @author Sly
 *
 */
public class Constants {
	
	/**
	 * Absolute Path of the root directory
	 */
	public static final String ABS_PATH = new File("").getAbsolutePath();
	
	// Animation Constants
	public static final int SINGLE_FRAME = 1;
	public static final float DEFAULT_SPEED = 15f;
	public static final int NO_ANIM = -1;
	public static final String DEFAULT_SPRITE = "DEFAULT";

	/**
	 *  Minimum Lerp "Change" Distance
	 *  
	 *  @see {@link NumberUtils#lerp}
	 */
	public static final float MIN_LERP_DIST = 0.001f;

	public static final String TEXTURE_FIELD_NAME = "TEXTURE_REF";
	
	public static final int DEPTH_DEFAULT = 0;
	
	// ERROR CONSTANTS
	public static final int ERR_NO_ROOM = -1;
	public static final int ERR_RES_LOAD_FAIL = -2;
	public static final int ERR_RES_NOT_FOUND = -3;

	// Background clear Color
	public static Color CLEAR_COLOR = new Color(0, 0, 0, 1);
}
