package io.sly.helix.game.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import io.sly.helix.Constants;
import io.sly.helix.game.Data;
import io.sly.helix.game.alarm.Alarm;
import io.sly.helix.game.alarm.Event;
import io.sly.helix.utils.math.Vector2D;

/**
 * GameObject that stores basic stuff for Game Objects, such as Alarms, a
 * position, direction, and access to the Data object
 * 
 * @author Sly
 *
 * @see {@link Data}, {@link Alarm}
 */
public abstract class GameObject {

	public static final Logger log = Logger.getLogger(GameObject.class.getCanonicalName());

	/**
	 * Next ID to be assigned
	 */
	private static Long ID_NEXT = 0L;
	
	//TODO: Tossing up whether or not I really want this system
	private static final List<Long> freeIds = new ArrayList<>();
	/**
	 * ID of the object
	 */
	public final Long id;

	/**
	 * Alarms to be used for timed events
	 */
	private Alarm[] alarm;

	/**
	 * Whether or not this object should be disposed
	 */
	private boolean shouldDispose;

	/**
	 * Position of the object
	 */
	private Vector2D pos;

	/**
	 * Direction the object travels in
	 */
	private Vector2D direction;

	/**
	 * Data of the application
	 */
	private final Data data;

	/**
	 * To be overridden as necessary. Ran before {@link GameObject#step}
	 * 
	 * @param delta - Time since last frame (seconds)
	 * 
	 * @see {@link GameObject#step}, {@link GameObject#postStep}
	 */
	protected void preStep(float delta) {
	}

	/**
	 * Basic step event for {@link GameObject}
	 * 
	 * @param delta - Time since last frame (seconds)
	 * 
	 * @see {@link GameObject#preStep}, {@link GameObject#postStep}
	 */
	protected abstract void step(float delta);

	/**
	 * To be overridden as necessary. Called after {@link GameObject#step} <br>
	 * Mainly used for cleanup
	 * 
	 * @param delta - Time since last frame (seconds)
	 * 
	 * @see {@link GameObject#preStep}, {@link GameObject#step}
	 */
	protected void postStep(float delta) {
	}

	/**
	 * Create a new GameObject
	 * 
	 * @param data - All the Game Data
	 * @param pos  - {@link Vector2D} to spawn the Object at
	 */
	public GameObject(Data data, Vector2D pos) {
		if(freeIds.size() == 0) {
			this.id = ID_NEXT++;
		} else {
			this.id = freeIds.get(0);
			freeIds.remove(0);
		}
		this.pos = pos;
		this.data = data;
		this.direction = new Vector2D(0, 0);
		this.initAlarms();
	}

	/**
	 * Initialize each alarm
	 * 
	 * @see {@link Alarm}, {@link Alarm#ALARM_COUNT}
	 */
	private void initAlarms() {
		alarm = new Alarm[Alarm.ALARM_COUNT];
		for (int i = 0; i < Alarm.ALARM_COUNT; i++) {
			alarm[i] = new Alarm();
		}
	}

	/**
	 * Update the game object
	 * 
	 * @param delta - Time since last frame (seconds)
	 */
	public final void update(float delta) {
		updateAlarms(delta);
		preStep(delta);
		step(delta);
		postStep(delta);
	}

	/**
	 * Update the alarms
	 * 
	 * @param delta - Time since last frame (seconds)
	 */
	private final void updateAlarms(float delta) {
		for (Alarm alarm : alarm) {
			alarm.update(delta);
		}
	}

	@SuppressWarnings("unchecked")
	// TODO: Optimize this
	/**
	 * Find the first instance of some {@link GameObject}
	 * 
	 * @param searchClass - Type of class to search for
	 * @return - the first instance of searchClass or null if none is found
	 */
	public final <T extends GameObject> T find(Class<T> searchClass) {
		for (GameObject object : data.getCurrentObjects()) {
			if (searchClass.isInstance(object))
				return (T) object;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Find the first instance of some {@link GameObject}
	 * 
	 * @param searchClass - Type of class to search for
	 * @return - the nearest instance of searchClass or null if none is found
	 */
	public final <T extends GameObject> T findNearest(Class<T> searchClass) {
		GameObject current = null;
		for (GameObject object : data.getCurrentObjects()) {
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

	/**
	 * Get the distance to another {@link GameObject}
	 * 
	 * @param other - Other Object to get distance to
	 * @return distance (float)
	 */
	public final float distTo(GameObject other) {
		return this.getPos().sub(other.getPos()).length();
	}

	/**
	 * Move towards {@link GameObject#direction} at a given speed
	 * 
	 * @param speed (px/frame)
	 */
	public final void move(float speed) {
		this.setPos(getPos().add(direction.multiply(speed)));
	}

	/**
	 * Move towards a {@link Vector2D} at a given speed
	 * 
	 * @param Vector2D - Vector2D to move towards
	 * @param speed - Speed to move at (px/frame)
	 */
	public final void moveTo(Vector2D Vector2D, float speed) {
		this.direction = Vector2D.sub(this.getPos()).getUnitVector();
		this.move(speed);
	}

	/**
	 * Move towards a {@link GameObject} at a given speed
	 * 
	 * @param target - Target to move towards
	 * @param speed  - Speed to move at (px/frame)
	 */
	public final void moveTo(GameObject target, float speed) {
		this.moveTo(target.getPos(), speed);
	}

	/**
	 * Move towards a {@link Vector2D} at {@link Constants#DEFAULT_SPEED}
	 * 
	 * @param Vector2D - Vector2D to move towards
	 */
	public final void moveTo(Vector2D Vector2D) {
		this.moveTo(Vector2D, Constants.DEFAULT_SPEED);
	}

	/**
	 * Move towards a {@link GameObject} at {@link Constants#DEFAULT_SPEED}
	 * 
	 * @param target - Target to move towards
	 */
	public final void moveTo(GameObject target) {
		this.moveTo(target, Constants.DEFAULT_SPEED);
	}

	// Getters and Setters
	public Vector2D getPos() {
		return pos;
	}

	public final void setPos(Vector2D other) {
		this.pos = other.copy();
	}

	public final void setPos(float x, float y) {
		this.pos.setX(x);
		this.pos.setY(y);
	}

	public final void addPos(float x, float y) {
		this.setPos(this.getPos().getX() + x, this.getPos().getY() + y);
	}

	public final Vector2D getDirection() {
		return direction;
	}

	public final void setDirection(Vector2D dir) {
		this.direction = dir;
	}

	public String toString() {
		return ("GameObject [pos=" + pos.toString() + ", " + "direction=" + direction.toString() + ", " + "]");
	}

	public final boolean willDispose() {
		return shouldDispose;
	}

	/**
	 * Free up global variables such as IDs here
	 */
	final void dispose() {
		freeIds.add(this.id);
	}
	
	public final void queueDispose() {
		shouldDispose = true;
	}

	public final Data getData() {
		return data;
	}

	public final Alarm getAlarm(int index) {
		return alarm[index];
	}

	public final void setAlarm(int index, int timer, Event action) {
		this.getAlarm(index).setAlarm(timer, action);
	}
}
