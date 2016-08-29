package net.mostlyoriginal.game.manager;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Manager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.physics.Gravity;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.resource.ZPos;

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
    private M<Renderable> mRenderable;
    private Archetype smokeParticle;
    private M<Angle> mAngle;
    private M<ZPos> mZPos;
    private M<Gravity> mGravity;

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
        smokeParticle = new ArchetypeBuilder()
                .add(Pos.class)
                .add(Anim.class)
                .add(Angle.class)
                .add(Renderable.class)
                .add(Schedule.class)
                .add(ZPos.class)
                .add(Scale.class)
                .add(Physics.class).build(world);
    }

    public void dust(float y, float x1, float x2, int particles , int layer )
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
            mRenderable.get(e).layer = layer;

            mSchedule.get(e).operation.add(
                    OperationFactory.sequence(
                            OperationFactory.tween(start, stop, 3f),
                            OperationFactory.deleteFromWorld()));

        }
    }

    public void cloud(int x, int y, int x2, int y2, int particles, int layer) {

        for(int i = 0; i<particles; i++) {
            int e = world.create(dustParticle);
            mPos.get(e).set(MathUtils.random(x,x2),MathUtils.random(y,y2));
            mAnim.get(e).id="dust_particle";
            Physics physics = mPhysics.get(e);
            physics.vx=MathUtils.random(-5f,5f);
            physics.vy=5 + MathUtils.random(5f,5f);
            physics.friction=0.01f;
            mScale.get(e).scale=MathUtils.random(0.5f,3f);
            mRenderable.get(e).layer = layer;

            mSchedule.get(e).operation.add(
                    OperationFactory.sequence(
                            OperationFactory.tween(start, stop, 3f),
                            OperationFactory.deleteFromWorld()));

        }
    }

    Vector2 tmp = new Vector2();

    public void smoke(float x, float y, float distance, int particles, int zPosLayer, int zPosLayerOffset, String[] ids, float minSize, float maxSize, float rotMin, float rotMax, boolean gravity, float vxMin, float vxMax, float vyMin, float vyMax, float duration) {

        for(int i = 0; i<particles; i++) {
            float size = MathUtils.random(minSize, maxSize);

            int rotation = MathUtils.random(0, 360);
            tmp.set(0, MathUtils.random(0,distance)).rotateRad(rotation);
            int e = world.create(smokeParticle);
            mPos.get(e).set(x+tmp.x - size/2,y+tmp.y - size/2);
            mAnim.get(e).id= ids[MathUtils.random(0, ids.length-1)];
            Physics physics = mPhysics.get(e);
            physics.vx=MathUtils.random(vxMin, vxMax);
            physics.vy=5 + MathUtils.random(vyMin, vyMax);
            physics.vr= MathUtils.random(rotMin, rotMax);
            if (gravity) {
                Gravity gravity1 = mGravity.create(e);
                gravity1.y=-1f;

            }
            physics.friction=0f;
            ZPos zPos = mZPos.get(e);
            zPos.z = zPosLayer;
            zPos.layerOffset = zPosLayerOffset;
            Angle angle = mAngle.get(e);
            angle.ox = (int) (size/2f * G.ZOOM);
            angle.oy = (int) (size/2f * G.ZOOM);
            angle.rotation = rotation;
            mScale.get(e).scale= size;
            mSchedule.get(e).operation.add(
                    OperationFactory.sequence(
                            OperationFactory.tween(start, stop, duration),
                            OperationFactory.deleteFromWorld()));

        }
    }


}
