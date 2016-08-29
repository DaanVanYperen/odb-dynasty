package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.resource.Stockpile;
import net.mostlyoriginal.game.system.resource.MinionSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;
import net.mostlyoriginal.game.system.ui.RiverDioramaSystem;

/**
 * Created by Daan on 28-8-2016.
 */
public class ProgressAlgorithmSystem extends IteratingSystem {

    public static final float COMPLETION_SCALE = 0.001f;
    protected StockpileSystem stockpileSystem;
    protected MinionSystem minionSystem;

    protected M<Stockpile> mStockpile;
    protected RiverDioramaSystem riverDioramaSystem;

    public boolean readyToProgress = false;
    public int projectedIncrease = 0;
    private boolean increaseAlert = false;

    public ProgressAlgorithmSystem() {
        super(Aspect.all(Stockpile.class));
    }

    public void progress()
    {
        if ( readyToProgress ) {
            readyToProgress=false;
            processSystem();

            System.out.println("Completion increase by " + (projectedIncrease * COMPLETION_SCALE));
            System.out.println("Alert? " + increaseAlert);

            stockpileSystem.alter(StockpileSystem.Resource.COMPLETION_PERCENTILE, projectedIncrease);
            stockpileSystem.alter(StockpileSystem.Resource.AGE, 1);
        }
    }

    // increase cost based on pyramid size.
    private int getProjectedIncrease() {
        int workforceProductivity = minionSystem.totalProductivity();
        return (int) (workforceProductivity *
                getProductivityFactor(stockpileSystem.get(StockpileSystem.Resource.COMPLETION))
                * getRiverFactor()
                * getBuildspeedFactor());
    }

    private float getRiverFactor() {
        switch (riverDioramaSystem.getState())
        {
            case RIVER_BLOOD: return 0.5f;
            case RIVER_WATER: return 1.5f;
            default: return 1f;
        }
    }

    private float getBuildspeedFactor() {
        return 1 + stockpileSystem.get(StockpileSystem.Resource.BUILDSPEED) * 0.25f;
    }

    private float getProductivityFactor(int pyramidLevel) {
        return MathUtils.clamp(20 - pyramidLevel*2,2,20);
    }

    @Override
    protected void process(int entityId) {

        // progress if needed.
        Stockpile stockpile = mStockpile.get(entityId);
        while ( stockpile.completionPercentile >= 1000 )
        {
            stockpile.completionPercentile -= 1000;
            stockpileSystem.alter(StockpileSystem.Resource.COMPLETION, 1);
        }

        projectedIncrease = getProjectedIncrease();
        increaseAlert = getRiverFactor() < 1f; // anything wrong?

        if ( readyToProgress )
        {

        }
    }

    public float getProgressPercentile() {
        return stockpileSystem.get(StockpileSystem.Resource.COMPLETION_PERCENTILE) * COMPLETION_SCALE;
    }

    public float getProjectedPercentile() {
        return projectedIncrease * COMPLETION_SCALE;
    }
}
