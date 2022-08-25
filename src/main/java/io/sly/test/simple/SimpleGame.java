package io.sly.test.simple;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;

import io.sly.helix.game.BaseGame;
import io.sly.test.simple.screens.MainScreen;

public class SimpleGame extends BaseGame {

	public SimpleGame() {
		super("io.sly", "Simple Test", 720, 480);
	}

	@Override
	public void init() {
		
	}

	@Override
	protected void start() {
		System.out.println("Created BaseGame");
	}
	
	public static void main(String[] args) {
		BaseGame game = new SimpleGame();
		game.addScreen(new MainScreen(game));
		new Lwjgl3Application(game, game.config);
	}
	
}
