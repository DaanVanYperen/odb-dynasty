package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.SystemInvocationStrategy;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.resource.Stockpile;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

/**
 * Created by Daan on 28-8-2016.
 */
public class ProgressAlgorithmSystem extends IteratingSystem {

    protected StockpileSystem stockpileSystem;
    protected M<Stockpile> mStockpile;

    public ProgressAlgorithmSystem() {
        super(Aspect.all(Stockpile.class));
    }

    public void progress()
    {
        stockpileSystem.alter(StockpileSystem.Resource.COMPLETION_PERCENTILE, getProjectedIncrease());
        stockpileSystem.alter(StockpileSystem.Resource.AGE, 1);
    }

    private int getProjectedIncrease() {
        return 100;
    }

    @Override
    protected void process(int entityId) {

        // progress if needed.
        Stockpile stockpile = mStockpile.get(entityId);
        System.out.println(stockpile.completionPercentile);
        while ( stockpile.completionPercentile >= 1000 )
        {
            System.out.println("Levelup!");
            stockpile.completionPercentile -= 1000;
            stockpileSystem.alter(StockpileSystem.Resource.COMPLETION, 1);
        }
    }
}
