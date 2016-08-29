package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
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
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Cheer;
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
    private M<ZPos> mZPos;
    private String[] SMOKE_PARTICLE_IDS = {"dust_particle"};
    private String[] FIRE_PARTICLE_IDS = {"FIRE PARTICLE 1","FIRE PARTICLE 2","FIRE PARTICLE 3","FIRE PARTICLE 4"};

    public FireballSystem() {
        super(Aspect.all(Fireball.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    float cooldown = 0;

    @Override
    protected void begin() {
        super.begin();
        cooldown -= world.delta;
        if (cooldown < 0) {
            cooldown = 1;
            spawn(G.CANVAS_WIDTH / 2, (int) (G.CANVAS_HEIGHT * 0.75f));
        }
    }

    Vector2 v2 = new Vector2();

    @Override
    protected void process(int e) {
        Physics physics = mPhysics.get(e);
        Pos pos = mPos.get(e);
        Angle angle = mAngle.get(e);
        angle.ox = AssetSystem.FIREBALL_WIDTH/2;
        angle.oy = AssetSystem.FIREBALL_HEIGHT/2;
        angle.rotation =
                v2.set(physics.vx, physics.vy).angle() + 90;

        Fireball fireball = mFireball.get(e);
        fireball.smokeCooldown -= world.delta;
        fireball.sparkCooldown -= world.delta;
        ZPos zPos = mZPos.get(e);

        if ( fireball.smokeCooldown <= 0 )
        {
            v2.rotateRad(90).nor().scl(20);
            fireball.smokeCooldown += 0.1f;
            smokeSystem.smoke(pos.xy.x + v2.x, pos.xy.y + v2.y, 4, 3, (int) zPos.z, zPos.layerOffset - 1, SMOKE_PARTICLE_IDS, 1f, 5f, -1, 1, false, 2f, 2f, 2f, 2f, 6f);
        }

        if ( fireball.sparkCooldown <= 0 )
        {
            fireball.sparkCooldown += 0.05f;
            smokeSystem.smoke(pos.xy.x + v2.x, pos.xy.y + v2.y, 4, 3, (int) zPos.z, zPos.layerOffset - 1, FIRE_PARTICLE_IDS, 1f, 3f, -180f, 180f, true, 2f, 2f, 2f, 2f, 2f);
        }

        clampToBorders(e);

        if ( zPos.height <= 5 )
        {
            explode(e);
        }
    }

    private void clampToBorders(int e ) {
        Physics physics = mPhysics.get(e);
        Pos pos = mPos.get(e);
        if ( pos.xy.x < 0 )
        {
            physics.vx = Math.abs(physics.vx);
        }
        if ( pos.xy.x > G.CANVAS_WIDTH - AssetSystem.FIREBALL_WIDTH*G.ZOOM )
        {
            physics.vx = -Math.abs(physics.vx);
        }
    }

    private void explode(int e) {
        Pos pos = mPos.get(e);
        ZPos zPos = mZPos.get(e);
        smokeSystem.smoke(pos.xy.x, pos.xy.y, 3, 50, (int) zPos.z, zPos.layerOffset - 1, FIRE_PARTICLE_IDS, 1f, 3f, -180f, 180f, true, -50f, 50f, 100f, 200f, 6f);
        world.delete(e);
    }

    public void spawn(int x, int y) {

        v2.set(0, 200).rotateRad(MathUtils.random(0,360));


        new DynastyEntityBuilder(world).pos(x, y)
                .with(Angle.class, Fireball.class)
                .gravity(0,-1f)
                .velocity(v2.x, v2.y, 0.2f)
                .tint("ffffffaa")
                .anim("FIREBALL")
                .renderable(800)
                .scale(G.ZOOM)
                .z(MathUtils.random(0, 48)).build();
    }
}
