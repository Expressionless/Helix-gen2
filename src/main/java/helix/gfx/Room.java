package helix.gfx;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;

import helix.game.BaseGame;
import helix.game.Data;

/**
 * Basic implementation of {@link ScreenAdapater}
 * Contains references to {@link Data} and {@link BaseGame}
 * @author Sly
 *
 */
public abstract class Room extends ScreenAdapter {

	private final String name;
	
	private boolean initialized = false;
	
	/**
	 * Basic Step event of the screen. Called before {@link Room#draw}
	 * @param delta - Time since last update (seconds)
	 */
	protected abstract void step(float delta);
	
	/**
	 * Basic Draw event of the screen. Called after {@link Room#step}
	 * @param delta - Time since last update (seconds)
	 */
	protected abstract void draw(float delta);
	
	/**
	 * Called when this screen is shown for the first time
	 */
	protected abstract void create();
	
	/**
	 * reference to main {@link Data}
	 */
	private final Data data;
	
	/**
	 * reference to main {@link BaseGame}
	 */
	private final BaseGame game;
	
	/**
	 * Create a new Screen and link it to a {@link BaseGame} (Does not add to Game's data)
	 * @param game - Game to link to 
	 */
	public Room(BaseGame game, String name) {
		this.game = game;
		this.name = name;
		this.data = game.getData();
	}
	
	/**
	 * Initialize the Screen (Can only be called once per instance)
	 */
	public void init() {
		if(initialized)
			return;
		
		create();
		initialized = true;
	}
	
	@Override
	public void show() {
		this.init();
	}
	
	@Override
	public void render(float delta) {
		this.step(delta);
		this.draw(delta);
	}
	
	// Getters and Setters
	public BaseGame getGame()  {
		return game;
	}
	
	public String getName() {
		return name;
	}
	
	public Data getData() {
		return data;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	// TODO: Implement multiple cameras lol
	public Camera getCurrentCamera() {
		return getData().getCurrentCamera();
	}
}
