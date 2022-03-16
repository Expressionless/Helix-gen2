package helix.game;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;

import helix.annotations.QueueAsset;
import helix.exception.HelixException;
import helix.exception.res.ResourceNotFoundException;
import helix.gfx.Room;
import helix.utils.ClassUtils;

public final class Data {
	public static final Logger log = Logger.getLogger(Data.class.getCanonicalName());

	private final BaseGame game;
	
	private Camera currentCamera;
	
	private Room currentRoom;
	
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
	
	public Data(BaseGame game) {
		this.game = game;
	}

	/**
	 * Adds a Room to the list of rooms and returns the Room ID if successful
	 * @param room - room to add
	 * @return the ID of the room if successfully added. -1 if room was not successfully added
	 * 
	 * @see {@link Room}
	 */
	public Integer addRoom(Room room) {
		log.info("Adding room: " + room.getName());
		if(this.rooms.add(room)) {
			return this.rooms.size() - 1;
		} else return -1;
	}

	
	/**
	 * Load all assets into the game
	 */
	public void loadTextures() throws HelixException {
		// Queue Resources
		Set<Class<?>> classes = null;
		Boolean isJarFile = ClassUtils.inJarFile();
		if(isJarFile) {
			try {
				classes = ClassUtils.getClassesFromJarFile();
				if(classes == null)
					classes = ClassUtils.getClasses();
			} catch (Exception e) {
				System.err.println("Failed to get Jar File");
				e.printStackTrace();
				System.exit(-1);
			}
		} else {
			classes = ClassUtils.getClasses();
		}

		QueueAsset queueAnnotation;
		Object fieldObject;
		File targetFile;
		
		for (Class<?> clazz : classes) {
			for (Field texField : clazz.getFields()) {
				if (!texField.isAnnotationPresent(QueueAsset.class))
					continue;
				
				queueAnnotation = texField.getAnnotation(QueueAsset.class);

				targetFile = new File(queueAnnotation.ref());
				if(!targetFile.exists() || targetFile.isDirectory()) {
					if(targetFile.isDirectory()) {
						log.severe("Target File is directory: " + queueAnnotation.ref());
					}
					throw new ResourceNotFoundException(queueAnnotation.ref());
				}
				
				// Attempt to set the texture field to the new value
				try {
					fieldObject = texField.get(clazz); // clazz = Mage.class
					texField.set(fieldObject, queueAnnotation.ref());
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

				this.getManager().load(queueAnnotation.ref(), queueAnnotation.type());
			}
		}

		log.info("Queued " + this.getManager().getQueuedAssets() + " assets to load");
		float progress = 0, lastProgress = 0;
		// Load Resources
		while (!this.getManager().update()) {
			// Check if progress got updated
			progress = this.getManager().getProgress();
			if (progress != lastProgress) {
				log.info("loading: " + this.getManager().getProgress());
				lastProgress = progress;
			}
		}

	}
	
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
	public Room getRoomById(Integer id) {
		return rooms.get(id);
	}
	
	public void setCurrentRoom(Room room) {
		this.currentRoom = room;
		game.setScreen(this.currentRoom);
	}
}
