package test.simple.rooms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import helix.game.BaseGame;
import helix.gfx.Room;

public class MainRoom extends Room {
	public static Color CLEAR_COLOR = new Color(0, 0.25f, 0, 1);

	public MainRoom(BaseGame game) {
		super(game, "MainRoom");
	}

	@Override
	protected void step(float delta) {
	}

	@Override
	protected void draw(float delta) {
		ScreenUtils.clear(CLEAR_COLOR);
	}

	@Override
	protected void create() {
		
	}

}
