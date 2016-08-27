package net.mostlyoriginal.game.system.endgame;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.EndgameReached;
import net.mostlyoriginal.game.component.resource.Stockpile;
import net.mostlyoriginal.game.system.dilemma.DilemmaSystem;

/**
 * Created by Daan on 27-8-2016.
 */
public class EndgameSystem extends IteratingSystem {

    protected M<Stockpile> mStockpile;
    protected DilemmaSystem dilemmaSystem;
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
        if ( stockpile.completion >= G.MAX_COMPLETION )
        {
            dilemmaSystem.superDynasty();
            mEndgameReached.create(e);
        }
    }

    private void checkForDeath(int e) {
        Stockpile stockpile = mStockpile.get(e);
        if ( stockpile.age >= stockpile.lifespan  )
        {
            dilemmaSystem.death();
            mEndgameReached.create(e);
        }
    }
}
