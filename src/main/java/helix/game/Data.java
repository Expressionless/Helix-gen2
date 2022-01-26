package helix.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;

import helix.gfx.Room;

public class Data {

	private Camera currentCamera;
	
	private final List<Room> rooms = new ArrayList<>();	
	
	/**
	 * An AssetManager to manage all assets
	 */
	private final AssetManager manager = new AssetManager();

	/**
	 * Called on creation of a new BaseGame<br>
	 * Override as necessary
	 * 
	 * @see {@link BaseGame#create()}
	 * @see {@link BaseGame}
	 */
	protected void init() {}
	
	
	// =============================== Getters and Setters =============================
	public AssetManager getManager() {
		return manager;
	}

	public List<Room> getRooms() {
		return rooms;
	}
	
	
	
	public Camera getCurrentCamera() {
		return currentCamera;
	}


	public void setCurrentCamera(Camera currentCamera) {
		this.currentCamera = currentCamera;
	}


	// TODO: Consider changing search algorithms? O(n) ain't pog
	public Room getRoomById(Long id) {
		for(Room room : rooms) {
			if(room.id == id) {
				return room;
			}
		}
		
		return null;
	}
	
	
}
