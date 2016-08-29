package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Angle;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.operation.common.Operation;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Ancient;
import net.mostlyoriginal.game.component.agent.Cheer;
import net.mostlyoriginal.game.component.agent.Tremble;
import net.mostlyoriginal.game.component.resource.Fireball;
import net.mostlyoriginal.game.component.resource.Minion;
import net.mostlyoriginal.game.component.resource.ZPos;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.manager.SmokeSystem;
import net.mostlyoriginal.game.system.endgame.EndgameSystem;

/**
 * Created by Daan on 27-8-2016.
 */
public class FireballSystem extends IteratingSystem {

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
    private M<Fireball> mFireball;
    private M<Angle> mAngle;
    private SmokeSystem smokeSystem;
    private M<Tremble> mTremble;
    private M<ZPos> mZPos;
    private String[] SMOKE_PARTICLE_IDS = {"dust_particle"};
    private String[] FIRE_PARTICLE_IDS = {"FIRE PARTICLE 1", "FIRE PARTICLE 2", "FIRE PARTICLE 3", "FIRE PARTICLE 4"};
    private MinionSystem minionSystem;

    public FireballSystem() {
        super(Aspect.all(Fireball.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    float cooldown = 0;
    int pendingFireballs = 0;
    int pendingRocks = 0;
    int pendingSpaceships = 0;

    public void queueFireball() {
        pendingFireballs++;
    }

    public void queueRock() {
        pendingRocks++;
    }

    public void queueSpaceship() {
        pendingSpaceships++;
    }

    @Override
    protected void begin() {
        super.begin();
        cooldown -= world.delta;
        if (cooldown < 0 && (pendingFireballs > 0 || pendingRocks > 0 || pendingSpaceships > 0)) {
            cooldown = 1;
            if (pendingFireballs > 0) {
                pendingFireballs--;
                spawnFireball();
            }
            if (pendingRocks > 0) {
                pendingRocks--;
                spawnRock();
            }
            if (pendingSpaceships > 0) {
                pendingSpaceships--;
                spawnSpaceship();
            }
        }
    }

    private void spawnSpaceship() {
        spawn(MathUtils.random(0, G.CANVAS_WIDTH), (int) (G.CANVAS_HEIGHT), "SPACESHIP", "ffffffff", true, false, false);
    }

    private void spawnRock() {
        spawn(MathUtils.random(0, G.CANVAS_WIDTH), (int) (G.CANVAS_HEIGHT), "ROCK", "ffffffff", true, false, true);
    }

    private void spawnFireball() {
        spawn(MathUtils.random(0, G.CANVAS_WIDTH), (int) (G.CANVAS_HEIGHT), "FIREBALL", "ffffffaa", true, true, true);
    }

    Vector2 v2 = new Vector2();

    @Override
    protected void process(int e) {
        Physics physics = mPhysics.get(e);
        Pos pos = mPos.get(e);
        Angle angle = mAngle.get(e);
        angle.ox = AssetSystem.FIREBALL_WIDTH / 2;
        angle.oy = AssetSystem.FIREBALL_HEIGHT / 2;
        angle.rotation =
                v2.set(physics.vx, physics.vy).angle() + 90;

        Fireball fireball = mFireball.get(e);
        fireball.smokeCooldown -= world.delta;
        fireball.sparkCooldown -= world.delta;
        ZPos zPos = mZPos.get(e);

        if (fireball.smokeCooldown <= 0 && fireball.hasSmoke) {
            v2.rotateRad(90).nor().scl(20);
            fireball.smokeCooldown += 0.1f;
            smokeSystem.smoke(pos.xy.x + v2.x, pos.xy.y + v2.y, 4, 3, (int) zPos.z, zPos.layerOffset - 1, SMOKE_PARTICLE_IDS, 1f, 5f, -1, 1, false, 2f, 2f, 2f, 2f, 6f);
        }

        if (fireball.sparkCooldown <= 0 && fireball.hasSparks) {
            fireball.sparkCooldown += 0.05f;
            smokeSystem.smoke(pos.xy.x + v2.x, pos.xy.y + v2.y, 4, 3, (int) zPos.z, zPos.layerOffset - 1, FIRE_PARTICLE_IDS, 1f, 3f, -180f, 180f, true, 2f, 2f, 2f, 2f, 2f);
        }

        clampToBorders(e);

        if (zPos.height <= 5) {
            crashMaybeExplode(e);
        }
    }

    private void clampToBorders(int e) {
        Physics physics = mPhysics.get(e);
        Pos pos = mPos.get(e);
        if (pos.xy.x < 0) {
            physics.vx = Math.abs(physics.vx);
        }
        if (pos.xy.x > G.CANVAS_WIDTH - AssetSystem.FIREBALL_WIDTH * G.ZOOM) {
            physics.vx = -Math.abs(physics.vx);
        }
    }

    private void crashMaybeExplode(int e) {
        Pos pos = mPos.get(e);
        ZPos zPos = mZPos.get(e);
        Fireball fireball = mFireball.get(e);
        smokeSystem.smoke(pos.xy.x, pos.xy.y, 3, 50, (int) zPos.z, zPos.layerOffset - 1,
                fireball.hasSparks ? FIRE_PARTICLE_IDS : SMOKE_PARTICLE_IDS, 1f, 3f, -180f, 180f, true, -50f, 50f, 100f, 200f, 6f);
        if (fireball.explodes) {
            assetSystem.playSfx("catapult_impact");
            world.delete(e);
            minionSystem.explodeMinions(pos.xy.x, pos.xy.y, 10 * G.ZOOM);
        } else {
            mAngle.get(e).rotation = 0;
            mPhysics.get(e).friction = 100f;
            mTremble.create(e).intensity = 1;
            mSchedule.create(e).operation.add(OperationFactory.sequence(OperationFactory.delay(1f), OperationFactory.remove(Tremble.class)));
            mFireball.remove(e);
        }
    }

    public void spawn(int x, int y, String id, String color, boolean smoke, boolean spark, boolean explodes) {

        assetSystem.playSfx("catapult");
        new DynastyEntityBuilder(world).pos(x, y)
                .with(Angle.class)
                .ancient()
                .gravity(0, -1f)
                .velocity(MathUtils.random(-400, 400), MathUtils.random(-100, -50), 0.2f)
                .tint(color)
                .anim(id)
                .renderable(800)
                .scale(G.ZOOM)
                .z(MathUtils.random(0, 48))
                .fireball(smoke, spark, explodes)
                .build();
    }

    private Tint invis = new Tint(1f, 1f, 1f, 0f);

    public void kill() {
        IntBag actives = world.getAspectSubscriptionManager().get(Aspect.all(Ancient.class)).getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            int entity = ids[i];
            mSchedule.create(entity).operation.add(
                    OperationFactory.sequence(
                            OperationFactory.tween(new Tint(mColor.get(entity).color), invis, 0.5f),
                            OperationFactory.deleteFromWorld()
                    ));
        }
    }
}
