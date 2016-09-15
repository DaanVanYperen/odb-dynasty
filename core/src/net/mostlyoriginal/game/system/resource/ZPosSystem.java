package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.FluidIteratingSystem;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.resource.ZPos;

import static net.mostlyoriginal.game.system.resource.MinionSystem.MINION_LAYER;

/**
 * Created by Daan on 28-8-2016.
 */
public class ZPosSystem extends FluidIteratingSystem {

    public ZPosSystem() {
        super(Aspect.all(Renderable.class, ZPos.class, Pos.class));
    }

    @Override
    protected void process(E e) {

        // min-y based on z.
        ZPos zPos = e._zPos();
        Pos pos = e._pos();
        float minY = G.CANVAS_HEIGHT / 2 - zPos.z / 2;
        if (pos.xy.y < minY) {
            pos.xy.y = minY;
        }

        zPos.height = pos.xy.y - minY;

        e.renderableLayer(MINION_LAYER + (int) zPos.z * 5 + zPos.layerOffset);

    }
}
