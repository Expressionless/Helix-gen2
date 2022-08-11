package io.sly.helix.game;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;

import io.sly.helix.annotations.QueueAsset;
import io.sly.helix.exception.HelixException;
import io.sly.helix.exception.res.ResourceNotFoundException;
import io.sly.helix.gfx.Screen;
import io.sly.helix.utils.ClassUtils;

public class Data {
	public static final Logger log = Logger.getLogger(Data.class.getCanonicalName());

	private final BaseGame game;
	
	private Camera currentCamera;
	
	private Screen currentScreen;
	
	private final List<Screen> screens = new ArrayList<>();	
	
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
	 * Adds a Screen to the list of rooms and returns the Screen ID if successful
	 * @param screen - screen to add
	 * @return the ID of the screen if successfully added. -1 if screen was not succesfully added
	 * 
	 * @see {@link Screen}
	 */
	public Integer addScreen(Screen screen) {
		if(this.screens.add(screen)) {
			return this.screens.size() - 1;
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

	public List<Screen> getScreens() {
		return screens;
	}
	
	
	
	public Camera getCurrentCamera() {
		return currentCamera;
	}


	public void setCurrentCamera(Camera currentCamera) {
		this.currentCamera = currentCamera;
	}


	// TODO: Consider changing search algorithms? O(n) ain't pog
	public Screen getScreenById(Long id) {
		for(Screen screen : screens) {
			if(screen.id == id) {
				return screen;
			}
		}
		
		return null;
	}
	
	
}
