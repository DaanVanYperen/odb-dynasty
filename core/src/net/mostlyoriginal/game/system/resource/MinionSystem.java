package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.resource.Minion;

/**
 * Created by Daan on 27-8-2016.
 */
public class MinionSystem extends EntityProcessingSystem {

    public static final int MINION_LAYER = 500;

    private M<Renderable> mRenderable;
    private M<Scale> mScale;
    private M<Pos> mPos;

    public MinionSystem() {
        super(Aspect.all(Minion.class));
    }

    @Override
    protected void process(Entity e) {

    }

    public Entity spawn() {
        Entity e = new EntityBuilder(world).with(
                new Bounds(0, 0, 0, 0),
                new Anim("minion-1"))
                .with(Pos.class,Scale.class,
                        Renderable.class).build();
        randomizeLocation(e);
        mScale.get(e).scale = 2;
        mRenderable.get(e).layer = MINION_LAYER;
        return e;

    }

    private void randomizeLocation(Entity entity) {
        mPos.get(entity).xy.set(
                MathUtils.random(0, G.CANVAS_WIDTH),
                MathUtils.random(G.CANVAS_HEIGHT/2 - 20, G.CANVAS_HEIGHT/2));
    }

    public void spawnMultiple(int count) {
        for(int i=0;i<count;i++) spawn();
    }
}
