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

    protected StockpileSystem stockpileSystem;
    protected MinionSystem minionSystem;

    protected M<Stockpile> mStockpile;
    protected RiverDioramaSystem riverDioramaSystem;

    private int projectedIncrease = 0;
    private boolean increaseAlert = false;

    public ProgressAlgorithmSystem() {
        super(Aspect.all(Stockpile.class));
    }

    public void progress()
    {
        processSystem();

        System.out.println("Completion increase by " +(projectedIncrease*0.001f));
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
            case 1: return 25;
            case 2: return 10;
            case 3: return 5f;
            case 4: return 1f;
            case 5: return 0.3f;
            case 6: return 0.2f;
            default: return 0.1f;
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
}
