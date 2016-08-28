package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.resource.MinionSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

/**
 * Sets the scene to reflect stockpiles.
 *
 * Created by Daan on 27-8-2016.
 */
public class DioramaSystem extends BaseSystem {

    private static final float SUN_DISTANCE = 64;
    protected MinionSystem minionSystem;
    protected StockpileSystem stockpileSystem;
    protected ScaffoldDioramaSystem scaffoldDioramaSystem;
    protected int minions = 0;
    protected int completion = -1;

    protected TagManager tagManager;
    protected M<Burrow> mBurrow;
    protected M<Pos> mPos;

    @Override
    protected void processSystem() {
        // 1. Spawn new minions.
        spawnMinions();
        // 2. Kill excessive minions.
        // 3. Grow tomb.
        scaleTomb();
        // 4. Shrink tomb.
        // 5. River state.
        // 6. situational events.
    }

    private void scaleTomb() {
        int completionNew = stockpileSystem.get(StockpileSystem.Resource.COMPLETION);
        final int completionDelta = completionNew - completion;
        if ( completionDelta != 0 )
        {
            Burrow burrow = mBurrow.get(tagManager.getEntity("pyramid"));
            burrow.targetPercentage = 1f - (completionNew / (float)G.MAX_COMPLETION);
            completion = completionNew;
            minionSystem.allCheer();

            int scaffoldHeight = (int) ((1f - burrow.targetPercentage) * 10f);
            scaffoldDioramaSystem.stack(2,8, MathUtils.clamp(scaffoldHeight,0,scaffoldHeight-5));
            scaffoldDioramaSystem.stack(3,7, MathUtils.clamp(scaffoldHeight,0,scaffoldHeight-3));
            scaffoldDioramaSystem.stack(4,6, MathUtils.clamp(scaffoldHeight,0,scaffoldHeight-1));
            scaffoldDioramaSystem.stack(5,5, MathUtils.clamp(scaffoldHeight,1,scaffoldHeight));
        }
    }

    private void spawnMinions() {
        final int minionDelta = stockpileSystem.get(StockpileSystem.Resource.WORKERS) - minions;
        if ( minionDelta > 0 )
        {
            minionSystem.spawnMultiple(minionDelta);
            minions += minionDelta;
        }
    }
}
