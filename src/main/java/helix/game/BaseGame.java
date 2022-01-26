package helix.game;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.logging.Logger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;

import helix.annotations.QueueAsset;
import helix.exception.HelixException;
import helix.exception.res.ResourceNotFoundException;
import helix.exception.room.NoDefaultRoomException;
import helix.gfx.Room;
import helix.utils.ClassUtils;

public abstract class BaseGame extends Game {
	public static final Logger log = Logger.getLogger(BaseGame.class.getCanonicalName());

	/**
	 * config for the application
	 */
	public final Lwjgl3ApplicationConfiguration config;
	private FPSLogger fps;
	
	/**
	 * Application dimensions
	 */
	public final int frameWidth, frameHeight;
	
	/**
	 * Application title
	 */
	public final String title;
	
	/**
	 * Application's data object. Keeps track of all the
	 * {@link helix.game.GameObject}s and {@link helix.game.objects.Entity}s and
	 * such
	 */
	private Data data;
	
	/**
	 * Ran as the last thing to do on launch
	 */
	protected abstract void start();
	
	public BaseGame(String title, int frameWidth, int frameHeight) {
		this.title = title;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		fps = new FPSLogger();
		
		// Initialize default config
		config = new Lwjgl3ApplicationConfiguration();
		config.setTitle(title);
		config.setWindowedMode(frameWidth, frameHeight);

		config.setIdleFPS(60);
		config.setForegroundFPS(90);
		config.useVsync(true);
		config.setResizable(false);
	}
	
	@Override
	public final void create() {

		// Load loading screen stuff
		this.data.getManager().finishLoading();
		this.data.setCurrentCamera(new OrthographicCamera());

		try {
			this.loadTextures();
		} catch(HelixException exception) {
			exception.printException();
			exception.terminateGame(this);
		}
		
		this.data.init();

		try {
			this.setRoom(this.data.getRooms().get(0));
		} catch (NullPointerException | IndexOutOfBoundsException exception) {
			HelixException noRoomException = new NoDefaultRoomException(exception);
			
			this.dispose();
			log.severe("Exiting with code: " + noRoomException.statusCode);
			Gdx.app.exit();
		}

		this.start();
	}
	
	public Data getData() {
		return data;
	}

	public void setRoom(Room room) {
		super.setScreen(room);
	}
	
	public void setRoom(Long roomId) {
		this.setRoom(this.getData().getRoomById(roomId));
	}
	
	
	
	/**
	 * Load all assets into the game
	 */
	private void loadTextures() throws HelixException {
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

				this.data.getManager().load(queueAnnotation.ref(), queueAnnotation.type());
			}
		}

		log.info("Queued " + this.data.getManager().getQueuedAssets() + " assets to load");
		float progress = 0, lastProgress = 0;
		// Load Resources
		while (!this.data.getManager().update()) {
			// Check if progress got updated
			progress = this.data.getManager().getProgress();
			if (progress != lastProgress) {
				log.info("loading: " + this.data.getManager().getProgress());
				lastProgress = progress;
			}
		}

	}
}
