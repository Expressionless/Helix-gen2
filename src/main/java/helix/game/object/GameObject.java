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
	 * Updates the globalPosition based on a camera
	 * @param camera
	 */
	public void updatePosition(Camera camera) {
		globalPosition.x = localPosition.x - camera.position.x;
		globalPosition.y = localPosition.y - camera.position.y;
	}
	
	public Room getParentRoom() {
		return parentRoom;
	}
}
