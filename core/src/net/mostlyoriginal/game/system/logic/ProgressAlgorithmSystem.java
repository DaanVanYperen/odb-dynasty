package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.agent.Hammer;
import net.mostlyoriginal.game.component.resource.Stockpile;
import net.mostlyoriginal.game.system.endgame.EndgameSystem;
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

    private boolean readyToProgress = false;
    public int projectedIncrease = 0;
    private boolean increaseAlert = false;
    public boolean tallying = false;
    private EntitySubscription hammerSubscription;
    public Integer score;
    private EndgameSystem endgameSystem;

    public boolean isReadyToProgress() {
        return readyToProgress;
    }

    @Override
    protected void initialize() {
        super.initialize();
        hammerSubscription = world.getAspectSubscriptionManager().get(Aspect.all(Hammer.class));
    }

    float tallyingAge = 0;

    public void setReadyToProgress(boolean readyToProgress) {
        this.readyToProgress = readyToProgress;
        tallying = true;
        tallyingAge=0;
    }

    public ProgressAlgorithmSystem() {
        super(Aspect.all(Stockpile.class));
    }

    public void progress()
    {
        if ( readyToProgress && !tallying ) {
            readyToProgress=false;
            processSystem();

            System.out.println("Completion increase by " + (projectedIncrease * COMPLETION_SCALE));
            System.out.println("Alert? " + increaseAlert);

            stockpileSystem.alter(StockpileSystem.Resource.COMPLETION_PERCENTILE, projectedIncrease);
            stockpileSystem.alter(StockpileSystem.Resource.AGE, 1);
        }
    }

    // increase cost based on pyramid size.
    private int getProjectedIncrease(boolean hammer) {
        int workforceProductivity = minionSystem.totalProductivity(hammer);
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
        return MathUtils.clamp(30 - pyramidLevel*2,2,30);
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

        if ( tallying )
        {
            if ( tallyingAge == 0  ) {
                projectedIncrease = getProjectedIncrease(true);
                increaseAlert = getRiverFactor() < 1f; // anything wrong?
                score = endgameSystem.getScore();
            }
            tallyingAge += world.delta;

            if ( !isHammersLeft() )
            {
                tallying=false;
            }
        }
    }

    private boolean isHammersLeft() {
        return !hammerSubscription.getEntities().isEmpty();
    }

    public float getProgressPercentile() {
        return stockpileSystem.get(StockpileSystem.Resource.COMPLETION_PERCENTILE) * COMPLETION_SCALE;
    }

    public float getProjectedPercentile() {
        return projectedIncrease * COMPLETION_SCALE;
    }
}
