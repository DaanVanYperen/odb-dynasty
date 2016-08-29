package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
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
public class ZPosSystem extends IteratingSystem {
    private M<Pos> mPos;
    private M<ZPos> mZPos;
    private M<Renderable> mRenderable;

    public ZPosSystem() {
        super(Aspect.all(Renderable.class, ZPos.class, Pos.class));
    }

    @Override
    protected void process(int e) {

        // min-y based on z.
        ZPos zPos = mZPos.get(e);
        Pos pos = mPos.get(e);
        float minY = G.CANVAS_HEIGHT / 2 - zPos.z / 2;
        if ( pos.xy.y < minY ) {
            pos.xy.y = minY;
        }

        zPos.height = pos.xy.y - minY;

        mRenderable.get(e).layer = MINION_LAYER + (int)zPos.z * 10 + zPos.layerOffset;

    }
}
