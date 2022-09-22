package io.sly.helix.utils.math;

import java.text.DecimalFormat;

/**
 * A 2 Dimensional Vector class with some basic operations
 * @author Sly
 *
 */
public class Vector2D {
	private static final DecimalFormat df = new DecimalFormat("0.00");

	/**
	 * Dimensions
	 */
	private float x, y;

	/**
	 * Create a new Vector2 with specified coordinates
	 * @param x (float)
	 * @param y (float)
	 */
	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new Vector2 with specified coordinates
	 * @param x
	 * @param y
	 */
	public Vector2D(Number x, Number y) {
		this(x.floatValue(), y.floatValue());
	}

	/**
	 * Sub another {@link Vector2D} from this one
	 * (this - other)
	 * @param other
	 * @return - a new resultant Vector2 from the operation
	 */
	public Vector2D sub(Vector2D other) {
		return new Vector2D(this.x - other.x, this.y - other.y);
	}

	/**
	 * Sub a set of coordinates from this Vector2
	 * (this - new Vector2(x, y))
	 * @param x
	 * @param y
	 * @return - a new resultant Vector2 from the operation
	 */
	public Vector2D sub(float x, float y) {
		return this.sub(new Vector2D(x, y));
	}

	/**
	 * Add a {@link Vector2D} to this one
	 * @param other - Other Vector2 to add
	 * @return - a new resultant Vector2 from the operation
	 */
	public Vector2D add(Vector2D other) {
		return new Vector2D(other.x + x, other.y + y);
	}

	/**
	 * Add a set of coordinates to this Vector2
	 * @param x
	 * @param y
	 * @return - a new resultant Vector2 from the operation
	 */
	public Vector2D add(float x, float y) {
		return this.add(new Vector2D(x, y));
	}

	/**
	 * Multiply this Vector2 by a scalar value
	 * @param scalar
	 * @return - a new resultant Vector2 from the operation
	 */
	public Vector2D multiply(float scalar) {
		return new Vector2D(this.x * scalar, this.y * scalar);
	}

	/**
	 * Calculate the length of this Vector2
	 * @return length (double)
	 */
	public float length() {
		return (float)Math.pow(lengthSq(), 0.5);
	}

	public float lengthSq() {
		double disX = Math.pow(this.x, 2);
		double disY = Math.pow(this.y, 2);
		return (float)(disX + disY);
	}
	
	public float getDistToSq(Vector2D other) {
		double disX = Math.pow(other.x - x, 2);
		double disY = Math.pow(other.y - y, 2);
		return (float)(disX + disY);
	}

	public float getDistTo(Vector2D other) {
		return (float)Math.pow(getDistToSq(other), 0.5);
	}

	/*
	 * Get the angle of this Vector2 in degrees,
	 * relative to the positive x axis
	 */
	public double getAngle() {
		double angle = 0;
		double sin = 0,
			   asin = 0,
			   cos = 0;
		
		double len = this.length();
		if (len == 0)
			return 0.0;

		sin = y / len;
		cos = x / len;
		asin = Math.asin(sin);
		
		boolean firstQuadrant = (sin >= 0 && cos >= 0);
		boolean secondQuadrant = (sin >= 0 && cos < 0);
		boolean thirdQuadrant = (sin < 0 && cos < 0);
		boolean fourthQuadrant = (sin < 0 && cos >= 0);
		
		if(firstQuadrant)
			angle = asin;
		else if(secondQuadrant)
			angle = Math.PI - asin;
		else if(thirdQuadrant)
			angle = Math.PI - asin;
		else if(fourthQuadrant)
			angle = 2 * Math.PI + asin;
		
		return Double.parseDouble(df.format(Math.toDegrees(angle)));
	}
	
	/**
	 * Get a Unit Vector of this Vector2. Unit Vectors always have length = 1
	 * @return a Unit Vector of this Vector2
	 */
	public Vector2D getUnitVector() {
		Vector2D vector = this.copy();
		double mag = vector.length();
		if(mag == 0)
			return new Vector2D(0 ,0);
		
		vector = new Vector2D(vector.getX() / mag, vector.getY() / mag);
		return vector;
	}

	/**
	 * Return a copy of this Vector2
	 * @return a copy of this Vector2
	 */
	public Vector2D copy() {
		return new Vector2D(x, y);
	}
		
	// Getters and Setters
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Vector2 ["
				+ "x=" + x
				+ ",y=" + y
				+ ",len=" + this.length()
				+ "]";
	}
}
