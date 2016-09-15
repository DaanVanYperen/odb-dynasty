package net.mostlyoriginal.game.system.ui;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.ui.Bar;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

import static com.artemis.E.E;

/**
 * Debug stockpile UI
 * Created by Daan on 27-8-2016.
 */
public class StockpileUISystem extends BaseSystem {

    private StockpileSystem stockpileSystem;
    private TagManager tagManager;

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
        return E()
                .pos(x,y)
                .renderable(500)
                .bar(label, icon, value, iconEmpty, valueEmpty)
                .tag("resource-" + label)
                .entity();

    }

    @Override
    protected void processSystem() {
        for (StockpileSystem.Resource resource : StockpileSystem.Resource.values()) {
            E barEntity = E(tagManager.getEntity("resource-" + resource.name()));
            barEntity.barValue(stockpileSystem.get(resource) / resource.getBarScale());
            barEntity.barValueEmpty(0);
        }
    }
}
