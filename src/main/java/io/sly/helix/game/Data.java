package io.sly.helix.game;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Game;

import io.sly.helix.Constants;
import io.sly.helix.annotations.QueueAsset;
import io.sly.helix.exception.HelixException;
import io.sly.helix.exception.res.ResourceNotFoundException;
import io.sly.helix.game.entities.GameObject;
import io.sly.helix.gfx.Animation;
import io.sly.helix.gfx.Screen;
import io.sly.helix.gfx.Sprite;
import io.sly.helix.utils.ClassUtils;
import io.sly.helix.utils.io.BinaryReader;
import io.sly.helix.utils.io.BinaryWriter;

public class Data {
	private static final Logger log = Logger.getLogger(Data.class.getCanonicalName());
	private static final String ABS_PATH = new File("").getAbsolutePath() + "/";

	private final String rootPackage;

	/**
	 * Read binary data with this. Call {@link BinaryWriter#close} when done writing
	 */
	protected BinaryReader reader;

	/**
	 * Write binary data with this Must call {@link BinaryWriter#close} when done
	 * writing
	 */
	protected BinaryWriter writer;

	private static Long ticks = 0L;


	private final BaseGame game;
	
	private Camera currentCamera;
	
	private Screen currentScreen;
	
	private double loadProgress;
	private boolean finishedLoading = false;

	private final List<GameObject> globalObjects = new ArrayList<>();
	
	private final List<Screen> screens = new ArrayList<>();

	/**
	 * Main Viewport in the application
	 */
	private Viewport viewport;
	
	/**
	 * An AssetManager to manage all assets
	 */
	private final AssetManager manager = new AssetManager();
	
	public Data(BaseGame game, String rootPackage) {
		this.game = game;
		this.rootPackage = rootPackage;
	}

	/**
	 * Create a {@link Sprite}
	 * 
	 * @param spriteName - Name of the sprite to add
	 * @param frameCount - no. of frames
	 * @param animTime   - anim time (ms)
	 * @return - a new {@link Sprite}
	 */
	public final Sprite createSprite(String spriteName, int frameCount, float animTime) {
		Texture texture = manager.get(spriteName);
		TextureRegion region = new TextureRegion(texture);
		Animation anim = new Animation(region, spriteName, frameCount, animTime);
		return new Sprite(anim);

	}

	/**
	 * Create a {@link Sprite} with a single frame and no animation time
	 * 
	 * @param spriteName - Name of the sprite to add
	 * @param frameCount - no. of frames
	 * @param animTime   - anim time (ms)
	 * @return - a new {@link Sprite}
	 */
	public final Sprite createSprite(String spriteName) {
		return this.createSprite(spriteName, Constants.SINGLE_FRAME, Constants.NO_ANIM);
	}
	
	/**
	 * Adds a Screen to the list of rooms and returns the Screen ID if successful
	 * @param screen - screen to add
	 * @return the ID of the screen if successfully added. -1 if screen was not succesfully added
	 * 
	 * @see {@link Screen}
	 */
	public Integer addScreen(Screen screen) {
		if(this.currentScreen == null) {
			this.setCurrentScreen(screen);
		}
		if(this.screens.add(screen)) {
			return this.screens.size() - 1;
		} else return -1;
	}

	
	/**
	 * Add a {@link GameObject} to the game data
	 * @param object
	 * @param global whether or not to make this object global between screens
	 * @return true if the object was added, false if object is null
	 * or if object was unable to be added to it's respective buffer
	 */
	public final Boolean addObject(GameObject object, boolean global) {
		if(object == null)
			return false;
		
		if(global) {
			return globalObjects.add(object);
		}

		return currentScreen.addObject(object);
	}

	
	/**
	 * Load all assets into the game
	 */
	public void queueAllTextures() throws HelixException {
		// Queue Resources
		Set<Class<?>> classes = ClassUtils.getClasses(rootPackage);
		QueueAsset queueAnnotation;
		Object fieldObject;
		File targetFile;
		
		for (Class<?> clazz : classes) {
			for (Field texField : clazz.getFields()) {
				
				if (!texField.isAnnotationPresent(QueueAsset.class))
					continue;
				
				queueAnnotation = texField.getAnnotation(QueueAsset.class);

				targetFile = new File(ABS_PATH + queueAnnotation.ref());
				if(!targetFile.exists() || targetFile.isDirectory()) {
					if(targetFile.isDirectory()) {
						log.error("Target File is directory: " + queueAnnotation.ref());
					}
					throw new ResourceNotFoundException(ABS_PATH + queueAnnotation.ref());
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
	}

	public boolean loadNextAsset() {
		if(this.getManager().update())
			return true;
		
		loadProgress = this.getManager().getProgress();
		return false;
	}

	public void loadTextures() {
		log.info("Queued " + this.getManager().getQueuedAssets() + " assets to load");
		// Load Resources
		if(this.getManager().getQueuedAssets() > 0) {
			boolean finishedLoading = false;
			while (!finishedLoading) {
				finishedLoading = loadNextAsset();
				// // Check if progress got updated
				// progress = this.getManager().getProgress();
				// if (progress != lastProgress) {
				// 	log.info("loading: " + this.getManager().getProgress());
				// 	lastProgress = progress;
				// }
			}

			finishedLoading = true;
		}
	}

	/**
	 * Override this as necessary. Gets called at the end of
	 * {@link Data#update(float)}
	 * 
	 * @param delta - Time since last frame (seconds)
	 */
	protected void step(float delta) {
	};

	/**
	 * Main update loop. Dispose of all entities that need disposing and then sort
	 * entities by {@link Entity#getDepth} Finally, update all objects and then run
	 * {@link Data#step}
	 * 
	 * @param delta - Time since last frame (seconds)
	 */
	public final void update(float delta) {
		ticks++;

		// this.disposeCore();

		// entities.sort(new Comparator<Entity>() {

		// 	public int compare(Entity e1, Entity e2) {
		// 		return (int) Math.signum(e2.getDepth() - e1.getDepth());
		// 	}

		// });

		// if (this.objectBuffer.size() > 0) {
		// 	this.objects.addAll(objectBuffer);
		// 	this.objectBuffer.clear();
		// }

		// for (GameObject object : objects) {
		// 	object.updateAlarms(delta);
		// 	object.update(delta);
		// }

		this.step(delta);
	}

	/**
	 * Gets called at end of {@link Data#render} Override as necessary to draw extra
	 * things through any abstract implementation of this class
	 * 
	 * @param batch - {@link SpriteBatch} to draw with
	 */
	protected void draw(SpriteBatch batch) {
	};

	/**
	 * Render every entity that needs rendering and then call {@link Data#draw}
	 * 
	 * @param batch - {@link SpriteBatch} to draw with
	 */
	public final void render(SpriteBatch batch) {
		// if (this.entityBuffer.size() > 0) {
		// 	this.entities.addAll(this.entityBuffer);
		// 	this.entityBuffer.clear();
		// }
		
		// for (Entity entity : entities) {
		// 	entity.render(batch);
		// }
		
		this.draw(batch);
	}

	/**
	 * Begin Reading with the {@link Data#reader} and read in from a specified path
	 * will return before reading if there is an instance of {@link Data#writer} or
	 * {@link Data#reader} already.
	 * 
	 * @param path - path to read in from, relative to the absolute directory
	 */
	public final boolean beginReading(String path) {
		if (writer != null)
			return false;
		if (reader != null)
			return false;

		reader = new BinaryReader(path);
		return true;
	}
	/**
	 * Stop reading from the {@link Data#reader}
	 */
	public final void stopReading() {
		if (reader == null)
			return;
		reader = null;
	}

	/**
	 * Begin writing to a file at some path with the {@link Data#writer} will return
	 * early if already reading or writing.
	 * 
	 * @param path - path to write to
	 */
	public final void beginWriting(String path) {
		if (writer != null)
			return;
		if (reader != null)
			return;

		writer = new BinaryWriter(path);
	}

	/**
	 * Stop writing with the {@link Data#writer} and safely close the stream
	 */
	public final void stopWriting() {
		if (writer == null)
			return;

		writer.close();
		writer = null;
	}
	
	// =============================== Getters and Setters =============================


	public AssetManager getManager() {
		return manager;
	}

	public List<Screen> getScreens() {
		return screens;
	}
	
	public List<GameObject> getCurrentObjects() {
		return currentScreen.getObjects();
	}

	public List<GameObject> getGlobalObjects() {
		return globalObjects;
	}
	
	public Camera getCurrentCamera() {
		return currentCamera;
	}


	public void setCurrentCamera(Camera currentCamera) {
		this.currentCamera = currentCamera;
	}

	public Screen getCurrentScreen() {
		return this.currentScreen;
	}

	public Screen setCurrentScreen(int id) {
		Screen s = this.getScreens().get(id);
		if(s != null)
			setCurrentScreen(s);
		return s;
	}

	public Screen goToNextScreen() {
		int currentScreenId = currentScreen.getId();
		Screen next = getScreen(currentScreenId + 1);
		
		// Loop
		if(next == null) {
			log.error("Could not find with screen with id of: " + (currentScreenId + 1));
			next = getScreen(0);
		}
		return setCurrentScreen(next);
	}

	public Screen getScreen(int id) {
		Screen s = this.getScreens().get(id);
		if(s == null) {
			log.error("Could not find screen with id: " + id);
		}

		return s;
	}

	public Screen setCurrentScreen(Screen screen) {
		log.info("Showing: " + ClassUtils.getClassName(screen));
		this.currentScreen = screen;
		((Game)this.getGame()).setScreen(screen);
		return this.currentScreen;
	}


	// TODO: Consider changing search algorithms? O(n) ain't pog
	public Screen getScreenById(Long id) {
		for(Screen screen : screens) {
			if(screen.getId() == id) {
				return screen;
			}
		}
		
		return null;
	}

	public final Viewport getViewport() {
		return viewport;
	}

	public final void setViewport(Viewport viewport) {
		this.viewport = viewport;
	}

	
	public BaseGame getGame() {
		return game;
	}

	public BinaryReader getReader() {
		return reader;
	}

	public Long getTicks() {
		return ticks;
	}

	public boolean hasFinishedLoading() {
		return finishedLoading;
	}
}
