package net.mostlyoriginal.game.system.agent;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.agent.Dead;
import net.mostlyoriginal.game.component.resource.Stockpile;
import net.mostlyoriginal.game.system.dilemma.DilemmaSystem;

/**
 * Created by Daan on 27-8-2016.
 */
public class PharaoDeathSystem extends IteratingSystem {

    protected M<Stockpile> mStockpile;
    protected DilemmaSystem dilemmaSystem;
    protected M<Dead> mDead;

    public PharaoDeathSystem() {
        super(Aspect.all(Stockpile.class));
    }

    @Override
    protected void process(int e) {
        Stockpile stockpile = mStockpile.get(e);
        if ( stockpile.age >= stockpile.lifespan && !mDead.has(e) )
        {
            mDead.create(e);
            dilemmaSystem.death();
        }
    }
}
