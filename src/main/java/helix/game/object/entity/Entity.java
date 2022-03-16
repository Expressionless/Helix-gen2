package helix.game.object.entity;

import helix.game.object.GameObject;
import helix.gfx.Room;

public abstract class Entity extends GameObject {

	/**
	 * 
	 * @param delta
	 */
	public abstract void render(float delta);
	
	public Entity(Room parentRoom, float x, float y) {
		super(parentRoom, x, y);
	}
}
