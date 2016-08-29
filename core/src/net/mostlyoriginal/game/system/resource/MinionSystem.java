package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.physics.Gravity;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Cheer;
import net.mostlyoriginal.game.component.agent.Hammer;
import net.mostlyoriginal.game.component.resource.Minion;
import net.mostlyoriginal.game.component.resource.ZPos;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.endgame.EndgameSystem;

import static net.mostlyoriginal.api.operation.OperationFactory.*;

/**
 * Created by Daan on 27-8-2016.
 */
public class MinionSystem extends IteratingSystem {

    public static final int MINION_LAYER = 600;
    public static final String TINT_INVISIBLE = "ffffff00";

    protected AssetSystem assetSystem;
    private M<Renderable> mRenderable;
    private M<Scale> mScale;
    private M<Pos> mPos;
    private M<Schedule> mSchedule;
    private M<Cheer> mCheer;
    private M<Invisible> mInvisible;
    private M<Minion> mMinion;
    private M<Anim> mAnim;
    private M<Physics> mPhysics;
    private EndgameSystem endgameSystem;
    private M<Tint> mColor;
    private M<Angle> mAngle;

    public MinionSystem() {
        super(Aspect.all(Minion.class));
    }

    @Override
    protected void process(int e) {
        Minion minion = mMinion.get(e);
    }

    /**
     * How productive is the total work force?
     *
     * @param hammer
     */
    public int totalProductivity(boolean hammer) {

        int result = 0;
        float delay = 0;

        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            int entity = ids[i];
            Minion minion = mMinion.get(entity);
            result += minion.productivity;

            if (hammer) {
                for(int j=0;j<minion.productivity;j++) {
                    spawnHammer(entity, delay);
                    delay += 0.1;
                }
            }
        }

        return result > 1 ? result : 1;
    }

    private Tint invis = new Tint(1f, 1f, 1f, 0f);
    private void spawnHammer(int e, float delayTime) {

        Pos pos = mPos.get(e);

        Entity entity = new DynastyEntityBuilder(world)
                .with(Hammer.class)
                .pos(pos.xy.x, pos.xy.y + 8 * G.ZOOM)
                .renderable(9000)
                .anim("GO-HAMMER")
                .scale(G.ZOOM)
                .tint("FFFFFF00")
                .schedule(
                        sequence(
                                delay(delayTime),
                                tween( new Tint("FFFFFF00"), new Tint("FFFFFFFF"), 0.1f),
                                tween(
                                        new Pos(pos.xy.x, pos.xy.y + 8 * G.ZOOM),
                                        new Pos(G.CANVAS_WIDTH / 2, 10 * G.ZOOM), 2f, Interpolation.pow2In),
                                deleteFromWorld()
                                ))
                .build();
        Angle angle = mAngle.create(entity);
        angle.ox = AssetSystem.HAMMER_WIDTH / 2 * G.ZOOM;
        angle.oy = AssetSystem.HAMMER_HEIGHT / 2 * G.ZOOM;
        Physics physics = mPhysics.create(entity);
        physics.friction=0;
        physics.vr = 400;
    }

    public Entity spawn(String id, int productivity) {
        System.out.println("Spawn " + id);
        Entity e = new DynastyEntityBuilder(world).with(
                new Bounds(0, 0, 0, 0),
                new Anim(id))
                .with(Pos.class, Scale.class,
                        Renderable.class, Physics.class, Gravity.class, Tint.class, ZPos.class)
                .schedule(tween(new Tint(TINT_INVISIBLE), new Tint("ffffffff"), 0.5f))
                .minion(productivity).build();
        randomizeLocation(e);
        Physics physics = mPhysics.get(e);
        physics.vy = 500;
        physics.friction = 20f;
        mColor.get(e).setHex(TINT_INVISIBLE);
        mScale.get(e).scale = G.ZOOM;
        mRenderable.get(e).layer = MINION_LAYER;
        return e;
    }

    public void explodeMinions(float x, float y, int distance) {
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            int entity = ids[i];
            Pos pos = mPos.get(entity);
            if (pos.xy.dst2(x, y) < distance * distance) {

                explode(entity);
            }
        }
    }

    private void explode(int entity) {
        mAngle.create(entity);
        Physics physics = mPhysics.create(entity);
        physics.vy = 500;
        physics.vx = MathUtils.random(-360, 360);
        physics.vr = MathUtils.random(-360, 360);
        physics.friction = 1f;

        mSchedule.create(entity).operation.add(
                OperationFactory.sequence(
                        OperationFactory.delay(MathUtils.random(1f, 2f)),
                        OperationFactory.deleteFromWorld()
                ));
    }

    public void allCheer() {
        assetSystem.playSfx("workers_cheering");
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            int entity = ids[i];
            mCheer.create(entity);
            mSchedule.create(entity).operation.add(
                    OperationFactory.sequence(
                            OperationFactory.delay(MathUtils.random(1f, 2f)),
                            OperationFactory.remove(Cheer.class)
                    ));
        }
    }

    public void future() {
        clear();
        spawnMultiple(endgameSystem.getSuccess().ordinal() * endgameSystem.getSuccess().ordinal() * 5, new String[]{
                "TOURIST MALE 1",
                "TOURIST MALE 2",
                "TOURIST MALE 3",
                "TOURIST MALE 4",
                "TOURIST FEMALE 1",
                "TOURIST FEMALE 2",
                "TOURIST FEMALE 3",
                "TOURIST FEMALE 4"
        }, 0);

        spawnMultiple(1, "GUIDE PUPPET", 0);
    }

    private void clear() {
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            int entity = ids[i];
            mInvisible.create(entity); // invisible, cause we still want to count them towards score.
        }
    }

    private void randomizeLocation(Entity entity) {
        mPos.get(entity).xy.set(
                MathUtils.random(8 * G.ZOOM, G.CANVAS_WIDTH - 8 * G.ZOOM), 0);
    }

    public void spawnMultiple(int count, String id, int productivity) {
        for (int i = 0; i < count; i++) spawn(id, productivity);
    }

    public void spawnMultiple(int count, String[] id, int productivity) {
        for (int i = 0; i < count; i++) spawn(id[MathUtils.random(0, id.length - 1)], productivity);
    }

    public void killCheapestUnit() {

        int productivity = 999;
        int cheapestId=-1;

        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            int entity = ids[i];
            Minion minion = mMinion.get(entity);
            if ( minion.productivity < productivity && !mSchedule.has(entity) )
            {
                productivity = minion.productivity;
                cheapestId = entity;
            }
        }

        if ( cheapestId != -1 )
        {
            explode(cheapestId);
        }
    }
}
