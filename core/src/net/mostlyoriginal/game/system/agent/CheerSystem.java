package net.mostlyoriginal.game.system.agent;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.agent.Cheer;
import net.mostlyoriginal.game.component.agent.Tremble;

/**
 * Created by Daan on 27-8-2016.
 */
public class CheerSystem extends IteratingSystem {

    protected M<Pos> mPos;
    protected M<Cheer> mCheer;

    public CheerSystem() {
        super(Aspect.all(Cheer.class, Pos.class));
    }

    @Override
    protected void process(int e) {

        final Cheer cheer = mCheer.get(e);
        cheer.age += world.delta * cheer.intensity * 100f;
        if ( cheer.age >= 180 ) cheer.age -= 180;

        revertTremble(e);
        setTremble(e, MathUtils.cosDeg(cheer.age-90f) * (4f * cheer.intensity));
    }

    @Override
    public void removed(int e) {
        revertTremble(e);
    }

    private void setTremble(int e, float offset) {
        Cheer tremble = mCheer.get(e);
        tremble.appliedY = offset;
        mPos.get(e).xy.y += tremble.appliedY;
    }

    private void revertTremble(int e) {
        Cheer tremble = mCheer.get(e);
        mPos.get(e).xy.y -= tremble.appliedY;
        tremble.appliedY=0;
    }
}
