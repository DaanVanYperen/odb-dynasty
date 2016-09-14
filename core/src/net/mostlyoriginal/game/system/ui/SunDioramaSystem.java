package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

import static com.artemis.E.E;

/**
 * Sets the scene to reflect stockpiles, sun specifically.
 * <p>
 * Created by Daan on 27-8-2016.
 */
public class SunDioramaSystem extends BaseSystem {

    private static final float SUN_DISTANCE = 64;
    protected StockpileSystem stockpileSystem;
    protected float sunPercentage = 0;

    protected TagManager tagManager;
    protected M<Pos> mPos;

    @Override
    protected void initialize() {
        createSun();
    }

    private void createSun() {
        E()
                .anim("SUN")
                .pos()
                .tag("sun")
                .renderable(-20)
                .scale(G.ZOOM)
                .entity();
    }


    @Override
    protected void processSystem() {
        float sunPercentageNew =
                MathUtils.clamp(stockpileSystem.get(StockpileSystem.Resource.AGE) / (float) stockpileSystem.get(StockpileSystem.Resource.LIFESPAN), 0, 1);
        float sunDelta = MathUtils.clamp(sunPercentageNew - sunPercentage, -1f, 1f);
        if (Math.abs(sunDelta) > 0.01) {
            sunPercentage += sunDelta * world.getDelta() * 5f;
        }

        float sunDegrees = sunPercentage * (180f + 20f) - 90f - 10f;

        Entity sun = tagManager.getEntity("sun");
        mPos.get(sun).xy.x = G.CANVAS_WIDTH / 2 - (AssetSystem.SUN_WIDTH * G.ZOOM) / 2f + MathUtils.sinDeg(sunDegrees) * SUN_DISTANCE * G.ZOOM;
        mPos.get(sun).xy.y = 133 * G.ZOOM - (AssetSystem.SUN_HEIGHT * G.ZOOM) / 2f + MathUtils.cosDeg(sunDegrees) * SUN_DISTANCE * G.ZOOM;
    }
}
