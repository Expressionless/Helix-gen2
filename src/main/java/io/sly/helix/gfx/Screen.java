package io.sly.helix.gfx;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;

import io.sly.helix.game.BaseGame;
import io.sly.helix.game.Data;
import io.sly.helix.game.entities.GameObject;
import io.sly.helix.utils.ClassUtils;

/**
 * Basic implementation of {@link ScreenAdapater}
 * Contains references to {@link Data} and {@link BaseGame}
 * @author Sly
 *
 */
public abstract class Screen extends ScreenAdapter {
	private static final Logger log = Logger.getLogger(Screen.class);
	
	private int id;

	private boolean initialized = false;

	private final List<GameObject> objects = new ArrayList<>();
	
	/**
	 * Basic Step event of the screen. Called before {@link Screen#draw}
	 * @param delta - Time since last update (seconds)
	 */
	protected abstract void step(float delta);
	
	/**
	 * Basic Draw event of the screen. Called after {@link Screen#step}
	 * @param delta - Time since last update (seconds)
	 */
	protected abstract void draw(float delta);
	
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
	 * Create a new Screen and link it to a {@link BaseGame}
	 * @param game - Game to link to 
	 */
	public Screen(BaseGame game, int id) {
		this.game = game;
		this.data = game.getData();
		this.id = id;
	}
	
	/**
	 * Initialize the Screen (Can only be called once per instance)
	 */
	private void init() {
		if(initialized) {
			return;
		}
		
		log.debug("Initializing " + ClassUtils.getClassName(this));
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

	public List<GameObject> getObjects() {
		return objects;
	}

	public boolean addObject(GameObject object) {
		return getObjects().add(object);
	}

	public int getId() {
		return this.id;
	}
}
