package net.mostlyoriginal.game.system.agent;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.FluidIteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.agent.Tremble;

import static com.artemis.E.E;

/**
 * Created by Daan on 27-8-2016.
 */
public class TrembleSystem extends FluidIteratingSystem {

    public TrembleSystem() {
        super(Aspect.all(Tremble.class, Pos.class));
    }

    @Override
    protected void process(E e) {

        Tremble tremble = e._tremble();
        tremble.age += world.delta * tremble.intensity;

        revertTremble(e);
        setTremble(e, MathUtils.sin(tremble.age * 100f) * (1.5f * tremble.intensity));
    }

    @Override
    public void removed(int e) {
        revertTremble(E(e));
    }

    private void setTremble(E e, float offset) {
        final Tremble tremble = e._tremble();
        tremble.appliedX = offset;
        e.posX( e.posX() + tremble.appliedX );
    }

    private void revertTremble(E e) {
        final Tremble tremble = e._tremble();
        e.posX( e.posX() - tremble.appliedX );
        tremble.appliedX=0;
    }
}
