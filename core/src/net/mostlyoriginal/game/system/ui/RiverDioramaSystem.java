package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

import static com.artemis.E.E;
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
        E()
                .anim("RIVER")
                .pos().renderable().scale().tint()
                .tag("river")
                .renderable(101)
                .pos(0, (133 - riverMarginY - AssetSystem.RIVER_HEIGHT) * G.ZOOM)
                .scale(G.ZOOM)
                .entity();
        E()
                .anim("RIVER-BLOOD")
                .pos().renderable().scale().tint()
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

    private void slowHide(E e) {
        e.removeScript().script(
                OperationFactory.tween(new Tint(e.tintColor()), invis, 3f));
    }

    private void slowReveal(E e) {
        e.removeScript().script(
                OperationFactory.tween(new Tint(e.tintColor()), vis, 3f));
    }

    public void blood() {
        slowHide(getRiver());
        slowReveal(getRiverBlood());
        state = RIVER_BLOOD;
    }

    private E getRiver() {
        return E(tagManager.getEntity("river"));
    }

    private E getRiverBlood() {
        return E(tagManager.getEntity("river-blood"));
    }


    @Override
    protected void processSystem() {
    }

    public RiverState getState() {
        return state;
    }
}
