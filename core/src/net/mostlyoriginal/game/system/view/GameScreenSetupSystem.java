package net.mostlyoriginal.game.system.view;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.util.Anims;

/**
 * @author Daan van Yperen
 */
@Wire
public class GameScreenSetupSystem extends PassiveSystem {

	AssetSystem assetSystem;

	@Override
	protected void initialize() {

		Anims.createCenteredAt(world,
				AssetSystem.DANCING_MAN_WIDTH,
				AssetSystem.DANCING_MAN_HEIGHT,
				"dancingman",
				Anims.scaleToScreenRoundedHeight(0.3f, AssetSystem.DANCING_MAN_HEIGHT));

	}

}
