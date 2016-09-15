package net.mostlyoriginal.game.system.agent;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.FluidIteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.agent.Cheer;

import static com.artemis.E.E;

/**
 * Created by Daan on 27-8-2016.
 */
public class CheerSystem extends FluidIteratingSystem {

    public CheerSystem() {
        super(Aspect.all(Cheer.class, Pos.class));
    }

    @Override
    protected void process(E e) {

        final Cheer cheer = e._cheer();
        cheer.age += world.delta * cheer.intensity * 100f;
        if ( cheer.age >= 180 ) cheer.age -= 180;

        revertTremble(e);
        setTremble(e, MathUtils.cosDeg(cheer.age-90f) * (4f * cheer.intensity));
    }

    @Override
    public void removed(int id) {
        revertTremble(E(id));
    }

    private void setTremble(E e, float offset) {
        final Cheer tremble = e._cheer();
        tremble.appliedY = offset;
        e.posY( e.posY() + tremble.appliedY );
    }

    private void revertTremble(E e) {
        final Cheer tremble = e._cheer();
        e.posY( e.posY() - tremble.appliedY );
        tremble.appliedY=0;
    }
}
