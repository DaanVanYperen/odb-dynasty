package net.mostlyoriginal.game.manager;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.E;
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

import static com.artemis.E.E;
import static net.mostlyoriginal.api.operation.OperationFactory.*;

/**
 * Created by Daan on 27-8-2016.
 */
public class SmokeSystem extends Manager {

    private Archetype dustParticle;


    private Tint start = new Tint(1f, 1f, 1f, 0.8f);
    private Tint stop = new Tint(1f, 1f, 1f, 0f);
    private Archetype smokeParticle;

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

    public void dust(float y, float x1, float x2, int particles, int layer) {
        for (int i = 0; i < particles; i++) {
            E e = E(world.create(dustParticle))
                    .pos(MathUtils.random(x1, x2), y + MathUtils.random(-5f, 5f))
                    .animId("dust_particle");
            Physics physics = e._physics();
            physics.vx = MathUtils.random(-5f, 5f);
            physics.vy = 5 + MathUtils.random(0, 10f);
            physics.friction = 0.01f;
            e.scale(MathUtils.random(0.5f, 3f))
                    .renderable(layer)
                    .script(
                            sequence(
                                    tween(start, stop, 3f),
                                    deleteFromWorld())
                    );

        }
    }

    public void cloud(int x, int y, int x2, int y2, int particles, int layer) {

        for (int i = 0; i < particles; i++) {
            E e = E(world.create(dustParticle))
                    .pos(MathUtils.random(x, x2), MathUtils.random(y, y2))
                    .animId("dust_particle");
            Physics physics = e._physics();
            physics.vx = MathUtils.random(-5f, 5f);
            physics.vy = 5 + MathUtils.random(5f, 5f);
            physics.friction = 0.01f;
            e
                    .scale(MathUtils.random(0.5f, 3f))
                    .renderable(layer)
                    .script(
                            sequence(
                                    tween(start, stop, 3f),
                                    deleteFromWorld())
                    );

        }
    }

    Vector2 tmp = new Vector2();

    public void smoke(float x, float y, float distance, int particles, int zPosLayer, int zPosLayerOffset, String[] ids, float minSize, float maxSize, float rotMin, float rotMax, boolean gravity, float vxMin, float vxMax, float vyMin, float vyMax, float duration) {

        for (int i = 0; i < particles; i++) {
            float size = MathUtils.random(minSize, maxSize);

            int rotation = MathUtils.random(0, 360);
            tmp.set(0, MathUtils.random(0, distance)).rotateRad(rotation);

            E e = E(world.create(smokeParticle))
                    .pos(x + tmp.x - size / 2, y + tmp.y - size / 2)
                    .animId(ids[MathUtils.random(0, ids.length - 1)]);
            Physics physics = e._physics();
            physics.vx = MathUtils.random(vxMin, vxMax);
            physics.vy = 5 + MathUtils.random(vyMin, vyMax);
            physics.vr = MathUtils.random(rotMin, rotMax);
            if (gravity) {
                Gravity gravity1 = e.gravity()._gravity();
                gravity1.y = -1f;

            }
            physics.friction = 0f;
            ZPos zPos = e._zPos();
            zPos.z = zPosLayer;
            zPos.layerOffset = zPosLayerOffset;
            Angle angle = e._angle();
            angle.ox = (int) (size / 2f * G.ZOOM);
            angle.oy = (int) (size / 2f * G.ZOOM);
            angle.rotation = rotation;
            e
                    .scale(size)
                    .script(
                            sequence(
                                    tween(start, stop, duration),
                                    deleteFromWorld()
                            )
                    );

        }
    }


}
