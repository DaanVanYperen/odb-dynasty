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
import net.mostlyoriginal.api.component.physics.Gravity;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Cheer;
import net.mostlyoriginal.game.component.resource.Fireball;
import net.mostlyoriginal.game.component.resource.Minion;
import net.mostlyoriginal.game.manager.AssetSystem;
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
        mFireball.get(e);
        Physics physics = mPhysics.get(e);
        mAngle.get(e).rotation =
                v2.set(physics.vx, physics.vy).angle();
    }

    public void spawn(int x, int y) {

        v2.set(0, 200).rotateRad(MathUtils.random(0,360));


        new DynastyEntityBuilder(world).pos(x, y)
                .with(Angle.class, Fireball.class)
                .gravity(0,-1f)
                .velocity(v2.x, v2.y, 0.2f)
                .anim("FIREBALL")
                .renderable(800)
                .scale(G.ZOOM)
                .z(MathUtils.random(0, 48)).build();
    }
}
