package io.sly.test.simple.screens;

import com.badlogic.gdx.Gdx;

import io.sly.helix.game.BaseGame;
import io.sly.helix.gfx.Screen;

public class MainScreen extends Screen {

	public MainScreen(BaseGame game) {
		super(game);
	}

	@Override
	protected void step(float delta) {
		
	}

	@Override
	protected void draw(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.35f ,1);
	}

	@Override
	protected void create() {
		
	}

}
