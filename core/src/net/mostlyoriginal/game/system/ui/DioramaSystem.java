package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.resource.MinionSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

/**
 * Sets the scene to reflect stockpiles.
 * <p>
 * Created by Daan on 27-8-2016.
 */
public class DioramaSystem extends BaseSystem {

    private static final float SUN_DISTANCE = 64;
    protected MinionSystem minionSystem;
    protected StockpileSystem stockpileSystem;
    protected ScaffoldDioramaSystem scaffoldDioramaSystem;
    protected int workers = 0;
    protected int completion = -1;

    protected TagManager tagManager;
    protected M<Burrow> mBurrow;
    protected M<Pos> mPos;
    private AssetSystem assetSystem;

    float chiselCooldown = 0;
    private int soldiers = 0;
    private int camels = 0;
    private int elephants = 0;

    @Override
    protected void processSystem() {
        // 1. Spawn new workers.
        spawnWorkers();
        spawnCamels();
        spawnElephants();
        spawnSoldiers();
        // 2. Kill excessive workers.
        // 3. Grow tomb.
        scaleTomb();
        // 4. Shrink tomb.
        // 5. River state.
        // 6. situational events.
        randomChisel();
    }

    private void randomChisel() {
        chiselCooldown -= world.delta;
        if (chiselCooldown <= 0) {
            chiselCooldown = MathUtils.random(0.2f, 3f);
            assetSystem.playRandomChisel();
        }
    }

    private void scaleTomb() {
        int completionNew = stockpileSystem.get(StockpileSystem.Resource.COMPLETION);
        final int completionDelta = completionNew - completion;
        if (completionDelta != 0) {
            Burrow burrow = mBurrow.get(tagManager.getEntity("pyramid"));
            burrow.targetPercentage = 1f - (completionNew / (float) G.MAX_COMPLETION);
            completion = completionNew;
            minionSystem.allCheer();

            int scaffoldHeight = (int) ((1f - burrow.targetPercentage) * 10);
            System.out.println(scaffoldHeight);
            scaffoldDioramaSystem.stack(2, 8, MathUtils.clamp(scaffoldHeight, 0, scaffoldHeight - 5));
            scaffoldDioramaSystem.stack(3, 7, MathUtils.clamp(scaffoldHeight, 0, scaffoldHeight - 3));
            scaffoldDioramaSystem.stack(4, 6, MathUtils.clamp(scaffoldHeight, 0, scaffoldHeight - 1));
            scaffoldDioramaSystem.stack(5, 5, MathUtils.clamp(scaffoldHeight, 1, scaffoldHeight));

            assetSystem.playSfx("pyramid_rise");
        }
    }

    private void spawnWorkers() {
        final int minionDelta = stockpileSystem.get(StockpileSystem.Resource.WORKERS) - workers;
        if (minionDelta > 0) {
            minionSystem.spawnMultiple(minionDelta, "WORKER", 1);
            workers += minionDelta;
        }
    }

    private void spawnSoldiers() {
        final int minionDelta = stockpileSystem.get(StockpileSystem.Resource.SOLDIERS) - soldiers;
        if (minionDelta > 0) {
            minionSystem.spawnMultiple(minionDelta, "SOLDIER", 0);
            soldiers += minionDelta;
        }
    }

    private void spawnCamels() {
        final int minionDelta = stockpileSystem.get(StockpileSystem.Resource.CAMELS) - camels;
        if (minionDelta > 0) {
            minionSystem.spawnMultiple(minionDelta, "CAMEL", 5);
            camels += minionDelta;
        }
    }

    private void spawnElephants() {
        final int minionDelta = stockpileSystem.get(StockpileSystem.Resource.ELEPHANTS) - elephants;
        if (minionDelta > 0) {
            minionSystem.spawnMultiple(minionDelta, "ELEPHANT", 10);
            elephants += minionDelta;
        }
    }
}
