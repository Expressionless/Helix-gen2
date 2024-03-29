package io.sly.helix.game.entities;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.sly.helix.game.Data;
import io.sly.helix.game.entities.collider.Collider;
import io.sly.helix.gfx.Sprite;
import io.sly.helix.utils.math.Vector2D;


/**
 * Entity class. Contains basic rendering and collision data and extends from
 * {@link GameObject}
 * 
 * @author Sly
 *
 */
public abstract class HelixEntity extends GameObject {
	
	/**
	 * Depth to render entity at (less depth means entity is rendered more towards
	 * background)
	 */
	private float depth;

	/**
	 * All sprites that the entity uses
	 */
	private HashMap<String, Sprite> sprites;
	/**
	 * Current sprite being rendered
	 */
	private Sprite currentSprite;

	/**
	 * Entity collider
	 */
	private Collider collider;

	/**
	 * Whether or not to Update and then Render this Entity. True by default
	 */
	private boolean isActive = true;

	/**
	 * Create a basic Entity
	 * 
	 * @param data - {@link helix.game.Data}
	 * @param pos
	 */
	public HelixEntity(Data data, Vector2D pos) {
		super(data, pos);

		this.sprites = new HashMap<String, Sprite>();
		this.currentSprite = null;
		this.collider = new Collider(this);
	}

	@Override
	protected void preStep(float delta) {
		this.updateDepth();
	}

	/**
	 * Render the Current sprite and run the abstract draw event after that
	 * 
	 * @param batch - SpriteBatch to draw the entity with
	 */
	public void render(SpriteBatch batch) {
		if (currentSprite != null) {
			currentSprite.draw(batch, this.getPos().getX(), this.getPos().getY());
		}

		draw(batch);
	}

	/**
	 * Override this as necessary. Updates the depth of the entity Default: depth =
	 * y coord of entity
	 */
	protected void updateDepth() {
		this.depth = this.getPos().getY();
	}

	/**
	 * Overrideable draw method
	 * 
	 * @param batch - SpriteBatch to draw the entity with
	 */
	protected void draw(SpriteBatch batch) {
	}

	/**
	 * Add a spriter to the entity's spriteset
	 * 
	 * @param spriteName - Name of the sprite
	 * @param numFrames  - Number of frames the sprite has in it
	 * @return - if the sprite was added successfully
	 */
	public final boolean addSprite(String spriteName, int numFrames) {
		return this.addSprite(spriteName, numFrames, -1);
	}

	/**
	 * Add a sprite to the entity's spriteset
	 * 
	 * @param spriteName - Name of the sprite
	 * @return - if the sprite was added successfully
	 */
	public final boolean addSprite(String spriteName) {
		return this.addSprite(spriteName, 1);
	}

	/**
	 * Add a sprite to the entity's spriteset
	 * 
	 * @param spriteName - Name of the sprite
	 * @param numFrames  - Number of frames the sprite has in it
	 * @param duration   - duration (in ms)
	 * @return - if the sprite was added successfully
	 */
	public final boolean addSprite(String spriteName, int numFrames, int duration) {
		if (sprites.containsKey(spriteName))
			return false;

		sprites.put(spriteName, this.getData().createSprite(spriteName, numFrames, duration));
		if (this.currentSprite == null)
			this.setSprite(spriteName);
		return true;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Find another the first occurrence of a specified {@link HelixEntity}
	 * 
	 * @param <T>         - Some class that extends {@link HelixEntity}
	 * @param searchClass - The class of the instance to be found
	 * @return - an instance of the searchClass type. Null if none exist
	 */
	public final <T extends HelixEntity> T findEntity(Class<T> searchClass) {
		for (GameObject object : this.getData().getCurrentObjects()) {
			if (searchClass.isInstance(object))
				return (T) object;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Find the nearest instance of a specified class that extends {@link HelixEntity}
	 * 
	 * @param <T>         - Some class that extends {@link HelixEntity}
	 * @param searchClass - The class of the instance to be found
	 * @return - the nearest instance of the searchClass type. Null if none exist
	 */
	public final <T extends HelixEntity> T findNearestEntity(Class<T> searchClass) {
		GameObject current = null;
		for (GameObject object : this.getData().getCurrentObjects()) {
			if (!searchClass.isInstance(object))
				continue;
			if (current == null) {
				current = object;
				continue;
			}

			float dis1 = this.getPos().getDistTo(object.getPos());
			float dis2 = this.getPos().getDistTo(current.getPos());
			if (dis1 < dis2)
				current = object;
		}
		if (current != null)
			return (T) current;
		else
			return null;
	}

	// Getters and Setters

	/**
	 * Set time in milliseconds
	 * 
	 * @param time (time in millis)
	 */
	public final void setCurrentAnimSpeed(float time) {
		this.currentSprite.getAnimation().setAnimTime(time);
	}

	/**
	 * Set the current Sprite
	 * 
	 * @param spriteName
	 */
	public final void setSprite(String spriteName) {
		if (!this.sprites.containsKey(spriteName))
			this.addSprite(spriteName);
		if (this.currentSprite == null || !this.currentSprite.equals(sprites.get(spriteName)))
			this.setSprite(sprites.get(spriteName));
	}

	public final void setSprite(Sprite s) {
		this.currentSprite = s;
	}

	/**
	 * Return the current Sprite
	 * 
	 * @return currentSprite
	 */
	public final Sprite getSprite() {
		return currentSprite;
	}

	public final float getWidth() {
		if (this.currentSprite != null)
			return currentSprite.getWidth();
		else
			return 0;
	}

	public final float getHeight() {
		if (this.currentSprite != null)
			return currentSprite.getHeight();
		else
			return 0;
	}

	public final float getDepth() {
		return this.depth;
	}

	public final void setDepth(float depth) {
		this.depth = depth;
	}

	public final Collider getCollider() {
		return collider;
	}

	/**
	 * Whether or not to Update and then Render this Entity. True by default
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Set whether to update and then render this entity or not
	 * @param active
	 */
	public void setActive(boolean active) {
		this.isActive = active;
	}
}
