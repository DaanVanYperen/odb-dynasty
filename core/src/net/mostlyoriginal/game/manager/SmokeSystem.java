package net.mostlyoriginal.game.manager;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Manager;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.ColorAnimation;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.physics.Gravity;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.operation.common.Operation;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

/**
 * Created by Daan on 27-8-2016.
 */
public class SmokeSystem extends Manager {

    private Archetype dustParticle;
    private M<Pos> mPos;
    private M<Schedule> mSchedule;
    private M<Physics> mPhysics;
    private M<Anim> mAnim;
    private M<Scale> mScale;


    private Tint start = new Tint(1f,1f,1f,0.8f);
    private Tint stop = new Tint(1f,1f,1f,0f);

    @Override
    protected void initialize() {
        super.initialize();

        dustParticle = new ArchetypeBuilder()
                .add(Pos.class)
                .add(Anim.class)
                .add(Renderable.class)
                .add(Schedule.class)
                .add(Scale.class)
                .add(Gravity.class)
                .add(Physics.class).build(world);
    }

    public void dust(float y, float x1, float x2, int particles )
    {
        for(int i = 0; i<particles; i++) {
            int e = world.create(dustParticle);
            mPos.get(e).set(MathUtils.random(x1,x2),y + MathUtils.random(-5f, 5f));
            mAnim.get(e).id="dust_particle";
            Physics physics = mPhysics.get(e);
            physics.vx=MathUtils.random(-5f,5f);
            physics.vy=5 + MathUtils.random(0,10f);
            physics.friction=0.01f;
            mScale.get(e).scale=MathUtils.random(0.5f,3f);

            mSchedule.get(e).operation.add(
                    OperationFactory.sequence(
                            OperationFactory.tween(start, stop, 3f),
                            OperationFactory.deleteFromWorld()));

        }
    }
}
