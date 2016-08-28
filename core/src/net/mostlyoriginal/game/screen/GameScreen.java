package net.mostlyoriginal.game.screen;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.ExtendedComponentMapperPlugin;
import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.system.camera.CameraShakeSystem;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.camera.EntityCameraSystem;
import net.mostlyoriginal.api.system.graphics.ColorAnimationSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.mouse.MouseCursorSystem;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.api.system.physics.GravitySystem;
import net.mostlyoriginal.api.system.physics.PhysicsSystem;
import net.mostlyoriginal.api.system.render.AnimRenderSystem;
import net.mostlyoriginal.api.system.render.ClearScreenSystem;
import net.mostlyoriginal.api.system.script.EntitySpawnerSystem;
import net.mostlyoriginal.game.manager.*;
import net.mostlyoriginal.game.system.agent.BurrowSystem;
import net.mostlyoriginal.game.system.agent.CheerSystem;
import net.mostlyoriginal.game.system.endgame.EndgameSystem;
import net.mostlyoriginal.game.system.agent.TrembleSystem;
import net.mostlyoriginal.game.system.dilemma.DilemmaSystem;
import net.mostlyoriginal.game.system.logic.ProgressAlgorithmSystem;
import net.mostlyoriginal.game.system.render.BarRenderSystem;
import net.mostlyoriginal.game.system.render.LabelRenderSystem;
import net.mostlyoriginal.game.system.render.ProgressRenderSystem;
import net.mostlyoriginal.game.system.resource.MinionSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;
import net.mostlyoriginal.game.system.resource.ZPosSystem;
import net.mostlyoriginal.game.system.ui.*;
import net.mostlyoriginal.plugin.OperationsPlugin;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class GameScreen extends WorldScreen {

	public static final String BACKGROUND_COLOR_HEX = "969291";

	@Override
	protected World createWorld() {
		RenderBatchingSystem renderBatchingSystem;
		return new World(new WorldConfigurationBuilder()
				.dependsOn(OperationsPlugin.class)
                .dependsOn(ExtendedComponentMapperPlugin.class)
				.dependsOn(OperationsPlugin.class)
				.with(
                        new GroupManager(),
						new TagManager(),
						new FontManager(),
						new StructureSystem(),
						new SmokeSystem(),

						new CameraSystem(1),
						new AssetSystem(),
						new ClearScreenSystem(Color.valueOf(BACKGROUND_COLOR_HEX)),

						new StockpileSystem(),
						new EntitySetupSystem(),

						new DilemmaSystem(),

						new EntitySpawnerSystem(),

						new MouseCursorSystem(),
						new MouseClickSystem(),
						new ButtonSystem(),

						new BurrowSystem(),

						new CollisionSystem(),
						new PhysicsSystem(),
						new GravitySystem(),
						new ZPosSystem(),

						new TrembleSystem(),
						new CheerSystem(),

						new ProgressAlgorithmSystem(),

//						new StockpileUISystem(),
						new ProgressUISystem(),

						new DioramaSystem(),
						new SunDioramaSystem(),
						new RiverDioramaSystem(),
                        new ScaffoldDioramaSystem(),

						new EndgameSystem(),

                        new MinionSystem(),

                        new ColorAnimationSystem(),

						new EntityCameraSystem(),
						new CameraShakeSystem(),

						// Replace with your own systems!
						renderBatchingSystem = new RenderBatchingSystem(),
						new AnimRenderSystem(renderBatchingSystem),
						new LabelRenderSystem(renderBatchingSystem),
						new BarRenderSystem(renderBatchingSystem),
						new ProgressRenderSystem(renderBatchingSystem)
				).build());

	}

}
