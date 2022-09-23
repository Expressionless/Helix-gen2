package io.sly.helix.utils.math;

/**
 * Basic Implementation of {@link com.badlogic.gdx.math.Rectangle}
 * with some added support for helix
 * @author Sly
 *
 */
public class Rectangle extends com.badlogic.gdx.math.Rectangle {
	
	/**
	 * Create a new rectangle with specified dimensions at coordinates
	 * @param x - X Position (px)
	 * @param y - Y Position (px)
	 * @param width - Width (px)
	 * @param height - Height (px)
	 */
	public Rectangle (float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	private static final long serialVersionUID = -2175678676093573672L;

	/**
	 * Check if this Rectangle contains some {@link Vector2D}
	 * @return Whether or not this Rectangle contains some Vector2
	 */
	public boolean contains(Vector2D v) {
		return super.contains(new com.badlogic.gdx.math.Vector2(v.getX(), v.getY()));
	}
	
}
