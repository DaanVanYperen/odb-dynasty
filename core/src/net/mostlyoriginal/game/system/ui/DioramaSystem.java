package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import net.mostlyoriginal.game.system.resource.MinionSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

/**
 * Sets the scene to reflect stockpiles.
 *
 * Created by Daan on 27-8-2016.
 */
public class DioramaSystem extends BaseSystem {

    protected MinionSystem minionSystem;
    protected StockpileSystem stockpileSystem;
    protected int minionCount = 0;

    @Override
    protected void processSystem() {
        // 1. Spawn new minions.
        final int minionDelta = stockpileSystem.get(StockpileSystem.Resource.WORKERS) - minionCount;
        if ( minionDelta > 0 )
        {
            minionSystem.spawnMultiple(minionDelta);
            minionCount += minionDelta;
        }
        // 2. Kill excessive minions.
        // 3. Grow tomb.
        // 4. Shrink tomb.
        // 5. River state.
        // 6. situational events.
    }
}
