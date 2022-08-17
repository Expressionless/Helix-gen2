package io.sly.helix.gfx;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;

import io.sly.helix.game.BaseGame;
import io.sly.helix.game.Data;
import io.sly.helix.game.entities.GameObject;

/**
 * Basic implementation of {@link ScreenAdapater}
 * Contains references to {@link Data} and {@link BaseGame}
 * @author Sly
 *
 */
public abstract class Screen extends ScreenAdapter {
	
	// Screens are less dynamic than game object (or at least should be) 
	// so we can treat them as such
	private static Long ID_NEXT = 0L;
	public final Long id = ID_NEXT++;
	
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
	public Screen(BaseGame game) {
		this.game = game;
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
}
