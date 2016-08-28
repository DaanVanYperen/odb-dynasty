package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.utils.EntityBuilder;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.ui.Bar;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

/**
 * Debug stockpile UI
 * Created by Daan on 27-8-2016.
 */
public class StockpileUISystem extends BaseSystem {

    private StockpileSystem stockpileSystem;
    private TagManager tagManager;
    private M<Pos> mPos;
    private M<Bar> mBar;
    private M<Renderable> mRenderable;

    @Override
    protected void initialize() {
        createStockpileUI();
    }

    private void createStockpileUI() {
        int index=1;
        for (StockpileSystem.Resource resource : StockpileSystem.Resource.values()) {
            createBar(10, G.CANVAS_HEIGHT - (index * 11),  resource.name(), "STOCKPILE-TICK", null, 0 , 0);
            index++;
        }
    }


    public Entity createBar(int x, int y, String label, String icon, String iconEmpty, int value, int valueEmpty) {
        Entity entity = new DynastyEntityBuilder(world)
                .with(Pos.class, Renderable.class)
                .with(new Bar(label, icon, value, iconEmpty, valueEmpty))
                .tag("resource-" + label)
                .build();
        mRenderable.get(entity).layer = 500;
        mPos.get(entity).xy.set(x,y);
        return entity;
    }

    @Override
    protected void processSystem() {
        for (StockpileSystem.Resource resource : StockpileSystem.Resource.values()) {
            Entity barEntity = tagManager.getEntity("resource-" + resource.name());
            mBar.get(barEntity).value = stockpileSystem.get(resource) / resource.getBarScale();
            mBar.get(barEntity).valueEmpty = 0;
        }
    }
}
