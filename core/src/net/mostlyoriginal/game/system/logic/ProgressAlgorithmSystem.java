package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
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

    public int projectedIncrease = 0;
    private boolean increaseAlert = false;

    public ProgressAlgorithmSystem() {
        super(Aspect.all(Stockpile.class));
    }

    public void progress()
    {
        processSystem();

        System.out.println("Completion increase by " +(projectedIncrease* COMPLETION_SCALE));
        System.out.println("Alert? " +increaseAlert);

        stockpileSystem.alter(StockpileSystem.Resource.COMPLETION_PERCENTILE, projectedIncrease);
        stockpileSystem.alter(StockpileSystem.Resource.AGE, 1);
    }

    // increase cost based on pyramid size.
    private int getProjectedIncrease() {
        int workforceProductivity = minionSystem.totalProductivity();
        return (int) (workforceProductivity * getProductivityFactor(stockpileSystem.get(StockpileSystem.Resource.COMPLETION)) * getRiverFactor());
    }

    private float getRiverFactor() {
        switch (riverDioramaSystem.getState())
        {
            case RIVER_BLOOD: return 0.5f;
            case RIVER_WATER: return 1.5f;
            default: return 1f;
        }
    }

    private float getProductivityFactor(int pyramidLevel) {
        switch (pyramidLevel) {
            case 0: return 50;
            case 1: return 55;
            case 2: return 40;
            case 3: return 30;
            case 4: return 20;
            case 5: return 10f;
            case 6: return 5f;
            case 7: return 3f;
            case 8: return 2f;
            default: return 1f;
        }
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
    }

    public float getProgressPercentile() {
        return stockpileSystem.get(StockpileSystem.Resource.COMPLETION_PERCENTILE) * COMPLETION_SCALE;
    }

    public float getProjectedPercentile() {
        return projectedIncrease * COMPLETION_SCALE;
    }
}
