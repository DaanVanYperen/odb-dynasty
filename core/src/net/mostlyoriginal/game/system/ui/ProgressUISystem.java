package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.systems.FluidIteratingSystem;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.ui.Progress;
import net.mostlyoriginal.game.system.logic.ProgressAlgorithmSystem;

import static com.artemis.E.E;

/**
 * Debug stockpile UI
 * Created by Daan on 27-8-2016.
 */
public class ProgressUISystem extends FluidIteratingSystem {

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
        return E().progress()
                .pos()
                .renderable(3005)
                .scale(G.ZOOM)
                .entity();
    }

    @Override
    protected void process(E e) {
        e.progressPlanned(progressAlgorithmSystem.getProjectedPercentile());
        e.progressValue(progressAlgorithmSystem.getProgressPercentile());
    }
}
