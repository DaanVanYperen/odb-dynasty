package net.mostlyoriginal.game;

import com.artemis.E;
import com.badlogic.gdx.Game;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.screen.detection.OdbFeatureScreen;

public class GdxArtemisGame extends Game {

	private static GdxArtemisGame instance;

	@Override
	public void create() {
		instance = this;
		restart();
	}

//	public void restart() {
//		setScreen(new OdbFeatureScreen());
//	}
	public void restart() {
		setScreen(new GameScreen());
	}

	public static GdxArtemisGame getInstance()
	{
		return instance;
	}
}
