package net.mostlyoriginal.game.system.endgame;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.EndgameReached;
import net.mostlyoriginal.game.component.resource.Stockpile;
import net.mostlyoriginal.game.system.dilemma.DilemmaSystem;
import net.mostlyoriginal.game.system.ui.ScaffoldDioramaSystem;

/**
 * Created by Daan on 27-8-2016.
 */
public class EndgameSystem extends IteratingSystem {

    protected M<Stockpile> mStockpile;
    protected DilemmaSystem dilemmaSystem;
    ScaffoldDioramaSystem scaffoldDioramaSystem;
    protected M<EndgameReached> mEndgameReached;

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

    public Success getSuccess() {
        return Success.BAD;
    }

    public enum Success {
        BAD,
        TINY,
        MODERATE,
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
        scaffoldDioramaSystem.kill();
    }
}
