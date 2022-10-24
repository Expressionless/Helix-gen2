package io.sly.helix.game;

import org.jboss.logging.Logger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;

import io.sly.helix.exception.HelixException;
import io.sly.helix.exception.screen.NoDefaultScreenException;
import io.sly.helix.gfx.Screen;

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
	 * {@link helix.game.GameObject}s and {@link HelixEntity.game.objects.Entity}s and
	 * such
	 */
	private Data data;
	
	/**
	 * Ran as the last thing to do on launch
	 */
	protected abstract void start();
	
	public BaseGame(String rootPackage, String title, int frameWidth, int frameHeight) {
		this.title = title;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		
		log.info(frameWidth + "x" + frameHeight);
		
		this.data = new Data(this, rootPackage);
		
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

	/**
	 * Ran before loading textures. Override as neccessary
	 * Default implementation:
	 * <ul>
	 * 	<li>Finish loading queued assets</li>
	 * 	<li>Create a new {@link OrthographicCamera} as the default camera</li>
	 * </ul>
	 * 
	 * @see {@link OrthographicCamera}
	 */
	protected void preLoad() {
		this.data.setCurrentCamera(new OrthographicCamera());
	}

	/**
	 * Ran after loading textures, before setting screen. Override as necessary
	 */
	protected void init() {}
	
	@Override
	public final void create() {

		// Load loading screen stuff
		preLoad();
		
		try {
			this.getData().queueAllTextures();
		} catch(HelixException exception) {
			System.err.println("Texture Loading error!");
			exception.printException();
			exception.terminateGame(this);
		}

		this.init();

		try {
			if(this.getData().getCurrentScreen() == null)
				this.setScreen(this.data.getScreens().get(0));
		} catch (NullPointerException | IndexOutOfBoundsException exception) {
			this.dispose();
			Gdx.app.exit();
			throw new NoDefaultScreenException();
		}

		this.start();
	}

	public Long getTicks() {
		return getData().getTicks();
	}
	
	public void setData(Data data) {
		this.data = data;
	}

	public Data getData() {
		return data;
	}

	private void setScreen(Screen screen) {
		super.setScreen(screen);
		getData().setCurrentScreen(screen);
	}
	
	public void addScreen(Screen screen) {
		this.data.addScreen(screen);
	}

	public Camera getCurrentCamera() {
		return getData().getCurrentCamera();
	}
}
