package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.B;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

import static net.mostlyoriginal.game.system.ui.RiverDioramaSystem.RiverState.RIVER_BLOOD;
import static net.mostlyoriginal.game.system.ui.RiverDioramaSystem.RiverState.RIVER_NONE;
import static net.mostlyoriginal.game.system.ui.RiverDioramaSystem.RiverState.RIVER_WATER;

/**
 * Sets the scene to reflect stockpiles, sun specifically.
 * <p>
 * Created by Daan on 27-8-2016.
 */
public class RiverDioramaSystem extends BaseSystem {

    private static final float SUN_DISTANCE = 64;
    protected StockpileSystem stockpileSystem;
    protected float sunPercentage = 0;

    protected TagManager tagManager;
    protected M<Pos> mPos;
    protected M<Invisible> mInvisible;
    protected M<Schedule> mSchedule;
    protected M<Tint> mColor;
    protected M<Anim> mAnim;
    private Tint vis = new Tint(1f, 1f, 1f, 1f);
    private Tint invis = new Tint(1f, 1f, 1f, 0f);
    private RiverState state =RIVER_NONE;

    public enum RiverState {
        RIVER_NONE,
        RIVER_BLOOD,
        RIVER_WATER
    }

    @Override
    protected void initialize() {
        createSun();
    }

    private void createSun() {
        int riverMarginY = 8;
        new B(world)
                .anim("RIVER")
                .with(Pos.class, Renderable.class, Scale.class, Tint.class)
                .tag("river")
                .renderable(101)
                .pos(0, (133 - riverMarginY - AssetSystem.RIVER_HEIGHT) * G.ZOOM)
                .scale(G.ZOOM)
                .entity();
        new B(world)
                .anim("RIVER-BLOOD")
                .with(Pos.class, Renderable.class, Scale.class, Tint.class)
                .tag("river-blood")
                .renderable(101)
                .pos(0, (133 - riverMarginY - AssetSystem.RIVER_HEIGHT) * G.ZOOM)
                .scale(G.ZOOM)
                .entity();
        clear();
    }

    public void clear() {
        slowHide(getRiver());
        slowHide(getRiverBlood());
        state = RIVER_NONE;
    }

    public void water() {
        slowHide(getRiverBlood());
        slowReveal(getRiver());
        state = RIVER_WATER;
    }

    private void slowHide(Entity e) {
        mSchedule.remove(e);
        mSchedule.create(e).operation.add(
                OperationFactory.tween(new Tint(mColor.get(e).color), invis, 3f));
    }

    private void slowReveal(Entity e) {
        mSchedule.remove(e);
        mSchedule.create(e).operation.add(
                OperationFactory.tween(new Tint(mColor.get(e).color), vis, 3f));
    }

    public void blood() {
        slowHide(getRiver());
        slowReveal(getRiverBlood());
        state = RIVER_BLOOD;
    }

    private Entity getRiver() {
        return tagManager.getEntity("river");
    }

    private Entity getRiverBlood() {
        return tagManager.getEntity("river-blood");
    }


    @Override
    protected void processSystem() {
    }

    public RiverState getState() {
        return state;
    }
}
