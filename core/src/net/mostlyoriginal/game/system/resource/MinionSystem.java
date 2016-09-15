package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.systems.FluidIteratingSystem;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Cheer;
import net.mostlyoriginal.game.component.resource.Minion;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.endgame.EndgameSystem;

import static com.artemis.E.E;
import static net.mostlyoriginal.api.operation.JamOperationFactory.tintBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.*;

/**
 * Created by Daan on 27-8-2016.
 */
public class MinionSystem extends FluidIteratingSystem {

    public static final int MINION_LAYER = 600;
    public static final String TINT_INVISIBLE = "ffffff00";

    protected AssetSystem assetSystem;
    private EndgameSystem endgameSystem;

    public MinionSystem() {
        super(Aspect.all(Minion.class));
    }

    @Override
    protected void process(E e) {
    }

    /**
     * How productive is the total work force?
     *
     * @param hammer
     */
    public int totalProductivity(boolean hammer) {

        int result = 0;
        float delay = 0;

        if (hammer) {
            assetSystem.playSfx("hammer_blop");
        }

        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            E e = E(ids[i]);
            Minion minion = e._minion();
            result += minion.productivity;

            if (hammer) {
                for (int j = 0; j < minion.productivity; j++) {
                    spawnHammer(e, delay);
                    delay += 0.1;
                }
            }
        }

        return result > 1 ? result : 1;
    }

    private Tint invis = new Tint(1f, 1f, 1f, 0f);

    private void spawnHammer(E e, float delayTime) {

        E()
                .hammer()
                .pos(e.posX(), e.posY() + 8 * G.ZOOM)
                .renderable(9000)
                .anim("GO-HAMMER")
                .scale(G.ZOOM)
                .tintHex("FFFFFF00")
                .angleOx(AssetSystem.HAMMER_WIDTH / 2 * G.ZOOM)
                .angleOy(AssetSystem.HAMMER_HEIGHT / 2 * G.ZOOM)
                .physicsFriction(0)
                .physicsVr(400)
                .script(
                        sequence(
                                delay(delayTime),
                                tween(new Tint("FFFFFF00"), new Tint("FFFFFFFF"), 0.1f),
                                tween(
                                        new Pos(e.posX(), e.posY() + 8 * G.ZOOM),
                                        new Pos(G.CANVAS_WIDTH / 2, 10 * G.ZOOM), 2f, Interpolation.pow2In),
                                deleteFromWorld()
                        ))
                .entity();
    }

    public Entity spawn(String id, int productivity, String deathSfx) {
        return E()
                .bounds(0, 0, 0, 0)
                .anim(id)
                .renderable(MINION_LAYER)
                .tintHex(TINT_INVISIBLE)
                .pos(MathUtils.random(8 * G.ZOOM, G.CANVAS_WIDTH - 8 * G.ZOOM), 0)
                .scale(G.ZOOM)
                .physicsVy(500)
                .physicsFriction(20f)
                .gravity().zPos()
                .script(tintBetween(Tint.TRANSPARENT, Tint.WHITE, 0.5f))
                .minion(productivity, deathSfx)
                .entity();
    }

    public void explodeMinions(float x, float y, int distance) {
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            E e = E(ids[i]);
            if (e.posXy().dst2(x, y) < distance * distance) {
                explode(e);
            }
        }
    }

    private void explode(E e) {
        e.angle();
        Physics physics = e._physics();
        physics.vy = 500;
        physics.vx = MathUtils.random(-360, 360);
        physics.vr = MathUtils.random(-360, 360);
        physics.friction = 1f;

        assetSystem.playSfx(e._minion().deathSfx);
        e.script(
                sequence(
                        delay(MathUtils.random(1f, 2f)),
                        deleteFromWorld()
                ));
    }

    public void allCheer() {
        assetSystem.playSfx("workers_cheering");
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            E(ids[i])
                    .cheer()
                    .script(
                            sequence(
                                    delay(MathUtils.random(1f, 2f)),
                                    remove(Cheer.class)
                            )
                    );
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
        }, 0, "worker_scream");

        spawnMultiple(1, "GUIDE PUPPET", 0, "worker_scream");
    }

    private void clear() {
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            E(ids[i]).invisible(); // invisible, cause we still want to count them towards score.
        }
    }

    public void spawnMultiple(int count, String id, int productivity, String deathSfx) {
        for (int i = 0; i < count; i++) spawn(id, productivity, deathSfx);
    }

    public void spawnMultiple(int count, String[] id, int productivity, String deathSfx) {
        for (int i = 0; i < count; i++) spawn(id[MathUtils.random(0, id.length - 1)], productivity, deathSfx);
    }

    public void killCheapestUnit() {

        int productivity = 999;
        E cheapest = null;

        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            E e = E(ids[i]);
            Minion minion = e._minion();
            if (minion.productivity < productivity && !e.hasScript()) {
                productivity = minion.productivity;
                cheapest = e;
            }
        }

        if (cheapest != null) {
            explode(cheapest);
        }
    }
}
