package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

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
    protected M<Anim> mAnim;

    @Override
    protected void initialize() {
        createSun();
    }

    private void createSun() {
        int riverMarginY = 8;
        new DynastyEntityBuilder(world)
                .with(new Anim("RIVER"))
                .with(Pos.class, Renderable.class, Scale.class)
                .tag("river")
                .renderable(101)
                .pos(0,(133 - riverMarginY - AssetSystem.RIVER_HEIGHT)*G.ZOOM)
                .scale(G.ZOOM)
                .build();
    }

    public void clear()
    {
        mInvisible.create(getRiver());
    }

    public void water()
    {
        mInvisible.remove(getRiver());
        mAnim.get(getRiver()).id="RIVER";
    }

    public void blood()
    {
        mInvisible.remove(getRiver());
        mAnim.get(getRiver()).id="RIVER-BLOOD";
    }

    private Entity getRiver() {
        return tagManager.getEntity("river");
    }


    @Override
    protected void processSystem() {
    }
}
