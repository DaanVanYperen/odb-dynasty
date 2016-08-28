package net.mostlyoriginal.game.manager;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.managers.TagManager;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.system.resource.MinionSystem;
import net.mostlyoriginal.game.util.Anims;

/**
 * Created by Daan on 28-8-2016.
 */
public class StructureSystem extends Manager {

    public static final int PYRAMID_BURROW_SPEED = 10;
    private MinionSystem minionSystem;
    private TagManager tagManager;

    private M<Burrow> mBurrow;
    private M<Pos> mPos;
    private M<Renderable> mRenderable;
    private M<Scale> mScale;

    public void createWifePyramid() {
        createStructure((int) (G.CANVAS_WIDTH * 0.75f), G.CANVAS_HEIGHT / 2, "PYRAMID-WIFE", "pyramid-wife", 1.0f, 0f, AssetSystem.PYRAMID_WIFE_WIDTH, AssetSystem.PYRAMID_WIFE_HEIGHT, PYRAMID_BURROW_SPEED*3);
        minionSystem.allCheer();
    }

    public void createPyramid() {
        createStructure(G.CANVAS_WIDTH / 2, G.CANVAS_HEIGHT / 2, "PYRAMID", "pyramid", 1.0f, 1.0f, AssetSystem.PYRAMID_WIDTH, AssetSystem.PYRAMID_HEIGHT, PYRAMID_BURROW_SPEED);
    }

    private void createStructure(int x, int y, String animId, String tag, float burrowPercentage, float burrowTargetPercentage, int width, int height, int speed) {
        Entity entity = Anims.createCenteredAt(world,
                width,
                height,
                animId,
                G.ZOOM);
        mPos.get(entity).xy.set(x - (width * G.ZOOM * 0.5f), y);

        if (tag != null) {
            tagManager.register(tag, entity);
        }

        Burrow burrow = mBurrow.create(entity);
        burrow.percentage = burrowPercentage;
        burrow.targetPercentage = burrowTargetPercentage;
        burrow.speed = speed;
        burrow.surfaceY = y;
    }
}
