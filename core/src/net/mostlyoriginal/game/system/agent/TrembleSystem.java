package net.mostlyoriginal.game.system.agent;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.component.agent.Tremble;

/**
 * Created by Daan on 27-8-2016.
 */
public class TrembleSystem extends IteratingSystem {

    protected M<Anim> mAnim;
    protected M<Pos> mPos;
    protected M<Tremble> mTremble;

    public TrembleSystem() {
        super(Aspect.all(Tremble.class, Pos.class));
    }

    @Override
    protected void process(int e) {

        Tremble tremble = mTremble.get(e);
        tremble.age += world.delta;

        revertTremble(e);
        setTremble(e, MathUtils.sin(tremble.age * 100f) * 1.5f);
    }

    @Override
    public void removed(int e) {
        revertTremble(e);
    }

    private void setTremble(int e, float offset) {
        Tremble tremble = mTremble.get(e);
        tremble.appliedX = offset;
        mPos.get(e).xy.x += tremble.appliedX;
    }

    private void revertTremble(int e) {
        Tremble tremble = mTremble.get(e);
        mPos.get(e).xy.x -= tremble.appliedX;
        tremble.appliedX=0;
    }
}
