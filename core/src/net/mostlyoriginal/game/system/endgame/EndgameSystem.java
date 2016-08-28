package net.mostlyoriginal.game.system.endgame;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.EndgameReached;
import net.mostlyoriginal.game.component.resource.Stockpile;
import net.mostlyoriginal.game.manager.EntitySetupSystem;
import net.mostlyoriginal.game.manager.StructureSystem;
import net.mostlyoriginal.game.system.dilemma.DilemmaSystem;
import net.mostlyoriginal.game.system.resource.MinionSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;
import net.mostlyoriginal.game.system.ui.ScaffoldDioramaSystem;

/**
 * Created by Daan on 27-8-2016.
 */
public class EndgameSystem extends IteratingSystem {

    protected M<Stockpile> mStockpile;
    protected StockpileSystem stockpileSystem;
    protected MinionSystem minionSystem;
    protected DilemmaSystem dilemmaSystem;
    ScaffoldDioramaSystem scaffoldDioramaSystem;
    protected M<EndgameReached> mEndgameReached;
    private StructureSystem structureSystem;
    private EntitySetupSystem entitySetupSystem;

    public EndgameSystem() {
        super(Aspect.all(Stockpile.class).exclude(EndgameReached.class));
    }

    @Override
    protected void process(int e) {
        checkForDeath(e);
        checkForSuperDynasty(e);
    }

    private void checkForSuperDynasty(int e) {
        Stockpile stockpile = mStockpile.get(e);
        if (stockpile.completion >= G.MAX_COMPLETION) {
            dilemmaSystem.ENDGAME(Success.SUPER);
            mEndgameReached.create(e);
        }
    }

    private static final int COMPLETION_SCORE_FACTOR = 20;
    public static final int FULL_COMPLETION_SCORE = G.MAX_COMPLETION * G.MAX_COMPLETION * COMPLETION_SCORE_FACTOR;
    public static final int HALF_COMPLETION_SCORE = (int) ((G.MAX_COMPLETION*0.75f) * (G.MAX_COMPLETION*0.75f) * COMPLETION_SCORE_FACTOR);
    public static final int QUARTER_COMPLETION_SCORE = (int) ((G.MAX_COMPLETION*0.5f) * (G.MAX_COMPLETION*0.5f) * COMPLETION_SCORE_FACTOR);
    public static final int SOME_COMPLETION_SCORE = 6 * 6 * COMPLETION_SCORE_FACTOR;
    public static final int MODERATE_COMPLETION_SCORE = 4 * 4 * COMPLETION_SCORE_FACTOR;
    public static final int TINY_COMPLETION_SCORE = 2 * 2 * COMPLETION_SCORE_FACTOR;

    public int getScore() {
        float completion = (stockpileSystem.get(StockpileSystem.Resource.COMPLETION) +
                (structureSystem.getObeliskCount()*0.5f));
        return
                (int) ((completion * completion * COMPLETION_SCORE_FACTOR) +
                                minionSystem.totalProductivity());
    }

    public Success getSuccess() {
        if ( getScore() >= FULL_COMPLETION_SCORE) {
            return Success.SUPER;
        } else if ( getScore() >= HALF_COMPLETION_SCORE) {
            return Success.AMAZING;
        } else if ( getScore() >= QUARTER_COMPLETION_SCORE) {
            return Success.GREAT;
        } else if ( getScore() >= SOME_COMPLETION_SCORE) {
            return Success.GOOD;
        } else if ( getScore() >= MODERATE_COMPLETION_SCORE) {
            return Success.MODERATE;
        } else if ( getScore() >= TINY_COMPLETION_SCORE) {
            return Success.TINY;
        } else{
            return Success.BAD;
        }
    }

    public enum Success {
        BAD,
        TINY,
        MODERATE,
        GOOD,
        GREAT,
        AMAZING,
        SUPER
    };

    private void checkForDeath(int e) {
        Stockpile stockpile = mStockpile.get(e);
        if (stockpile.age >= stockpile.lifespan) {
            dilemmaSystem.ENDGAME(getSuccess());
            mEndgameReached.create(e);
        }
    }

    public void setFutureScene() {
        entitySetupSystem.createSkyscrapers();
        scaffoldDioramaSystem.kill();
        minionSystem.future();
    }
}
