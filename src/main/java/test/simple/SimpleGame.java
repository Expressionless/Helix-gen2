package test.simple;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;

import helix.game.BaseGame;

public class SimpleGame extends BaseGame {

	public SimpleGame() {
		super("Simple Test", 720, 480);
	}

	@Override
	protected void start() {
		System.out.println("Created BaseGame");
	}
	
	public static void main(String[] args) {
		BaseGame game = new SimpleGame();
		new Lwjgl3Application(game, game.config);
	}
	
}
