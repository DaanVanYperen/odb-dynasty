package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.ui.Bar;
import net.mostlyoriginal.game.component.ui.Progress;
import net.mostlyoriginal.game.system.logic.ProgressAlgorithmSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

/**
 * Debug stockpile UI
 * Created by Daan on 27-8-2016.
 */
public class ProgressUISystem extends IteratingSystem {

    private M<Progress> mProgress;
    private ProgressAlgorithmSystem progressAlgorithmSystem;

    public ProgressUISystem() {
        super(Aspect.all(Progress.class));
    }

    @Override
    protected void initialize() {
        createStockpileUI();
    }

    private void createStockpileUI() {
        createBar();
    }


    public Entity createBar() {
        Entity entity = new DynastyEntityBuilder(world)
                .with(Pos.class, Renderable.class)
                .with(new Progress())
                .renderable(3005)
                .scale(G.ZOOM)
                .build();
        return entity;
    }

    @Override
    protected void process(int entityId) {
        mProgress.get(entityId).planned = progressAlgorithmSystem.getProjectedPercentile();
        mProgress.get(entityId).value = progressAlgorithmSystem.getProgressPercentile();
    }
}
