package helix.game.object;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import helix.gfx.Room;

public abstract class GameObject {

	/**
	 * The on-screen position of the Game Object
	 */
	private Vector2 globalPosition;
	
	/**
	 * The position relative to the parent room of the Game Object
	 */
	private Vector2 localPosition;
	
	/**
	 * The {@link Room} the GameObject resides in
	 */
	private final Room parentRoom;
	
	/**
	 * Create a new GameObject, setting its localPosition to x, y
	 * @param x
	 * @param y
	 */
	public GameObject(Room parentRoom, float x, float y) {
		this.localPosition = new Vector2(x, y);
		this.globalPosition = Vector2.Zero;
		this.parentRoom = parentRoom;
	}
	
	/**
	 * To be overridden as necessary. Ran before {@link GameObject#step}
	 * 
	 * @param delta - Time since last frame (seconds)
	 * 
	 * @see {@link GameObject#step}, {@link GameObject#postStep}
	 */
	protected void preStep(float delta) {
	}

	/**
	 * Basic step event for {@link GameObject}
	 * 
	 * @param delta - Time since last frame (seconds)
	 * 
	 * @see {@link GameObject#preStep}, {@link GameObject#postStep}
	 */
	protected abstract void step(float delta);

	/**
	 * To be overridden as necessary. Called after {@link GameObject#step} <br>
	 * Mainly used for cleanup
	 * 
	 * @param delta - Time since last frame (seconds)
	 * 
	 * @see {@link GameObject#preStep}, {@link GameObject#step}
	 */
	protected void postStep(float delta) {
	}
	
	public void update(float delta) {
		preStep(delta);
		step(delta);
		postStep(delta);
	}
	
	/**
	 * Updates the globalPosition based on a camera
	 * @param camera
	 */
	public void updatePosition(Camera camera) {
		globalPosition.x = localPosition.x - camera.position.x;
		globalPosition.y = localPosition.y - camera.position.y;
	}
	
	// Getters and Setters
	
	public Room getParentRoom() {
		return parentRoom;
	}
}
