package net.mostlyoriginal.game.manager;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.system.resource.MinionSystem;
import net.mostlyoriginal.game.util.Anims;

import static net.mostlyoriginal.api.operation.OperationFactory.sequence;

/**
 * Created by Daan on 28-8-2016.
 */
public class StructureSystem extends Manager {

    public static final int PYRAMID_BURROW_SPEED = 10;
    private MinionSystem minionSystem;
    private TagManager tagManager;
    private GroupManager groupManager;

    private M<Burrow> mBurrow;
    private M<Pos> mPos;
    private M<Renderable> mRenderable;
    private M<Scale> mScale;
    private M<Schedule> mSchedule;

    public void createWifePyramid() {
        createStructure((int) (G.CANVAS_WIDTH * 0.75f), G.CANVAS_HEIGHT / 2, "PYRAMID-WIFE", "pyramid-wife", 1.0f, 0f, AssetSystem.PYRAMID_WIFE_WIDTH, AssetSystem.PYRAMID_WIFE_HEIGHT, PYRAMID_BURROW_SPEED*3, -10);
        minionSystem.allCheer();
    }

    public void createObelisk() {
        Entity entity = createStructure(MathUtils.randomBoolean() ? (int) MathUtils.random(G.CANVAS_WIDTH * 0.05f, G.CANVAS_WIDTH * 0.35f) :
                (int) MathUtils.random(G.CANVAS_WIDTH * 0.65f, G.CANVAS_WIDTH * 0.95f) ,
                G.CANVAS_HEIGHT / 2,
                "OBELISK",
                "obelisk", 1.0f, 0f, AssetSystem.OBELISK_WIDTH, AssetSystem.OBELISK_HEIGHT, PYRAMID_BURROW_SPEED * 6, -5);
        groupManager.add(entity, "obelisks");

        minionSystem.allCheer();
    }

    public void createPyramid() {
        createStructure(G.CANVAS_WIDTH / 2, G.CANVAS_HEIGHT / 2, "PYRAMID", "pyramid", 1.0f, 1.0f, AssetSystem.PYRAMID_WIDTH, AssetSystem.PYRAMID_HEIGHT, PYRAMID_BURROW_SPEED, 0);
    }

    private Entity createStructure(int x, int y, String animId, String tag, float burrowPercentage, float burrowTargetPercentage, int width, int height, int speed, int layer) {
        Entity entity = Anims.createCenteredAt(world,
                width,
                height,
                animId,
                G.ZOOM);
        mRenderable.get(entity).layer = layer;
        mPos.get(entity).xy.set(x - (width * G.ZOOM * 0.5f), y);

        if (tag != null) {
            tagManager.register(tag, entity);
        }

        Burrow burrow = mBurrow.create(entity);
        burrow.percentage = burrowPercentage;
        burrow.smokeLayer = layer + 1;
        burrow.targetPercentage = burrowTargetPercentage;
        burrow.speed = speed;
        burrow.surfaceY = y;

        return entity;
    }

    public void destroyObelisks() {
        ImmutableBag<Entity> entities = groupManager.getEntities("obelisks");
        for (int i = 0, s = entities.size(); i < s; i++) {
            Entity entity = entities.get(i);
            Burrow burrow = mBurrow.get(entity);
            burrow.targetPercentage=1.0f;
            burrow.speed *= 5;
            mSchedule.create(entity).operation.add(sequence(OperationFactory.delay(3), OperationFactory.deleteFromWorld()));
        }
    }
}
