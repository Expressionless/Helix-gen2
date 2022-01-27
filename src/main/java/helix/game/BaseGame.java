package helix.game;

import java.util.logging.Logger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;

import helix.exception.HelixException;
import helix.exception.room.NoDefaultRoomException;
import helix.gfx.Room;

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
		
		this.data = new Data(this);
		
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
			this.data.loadTextures();
		} catch(HelixException exception) {
			System.err.println("Texture Loading error!");
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
}
