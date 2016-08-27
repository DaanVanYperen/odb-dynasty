package net.mostlyoriginal.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.GdxArtemisGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = G.CANVAS_WIDTH;
		config.height = G.CANVAS_HEIGHT;
		new LwjglApplication(new GdxArtemisGame(), config);
	}
}
