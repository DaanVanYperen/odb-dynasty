package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.Bag;
import com.artemis.utils.EntityBuilder;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Cheer;
import net.mostlyoriginal.game.component.resource.Minion;

import static com.badlogic.gdx.utils.JsonValue.ValueType.array;

/**
 * Created by Daan on 27-8-2016.
 */
public class MinionSystem extends IteratingSystem {

    public static final int MINION_LAYER = 500;

    private M<Renderable> mRenderable;
    private M<Scale> mScale;
    private M<Pos> mPos;
    private M<Schedule> mSchedule;
    private M<Cheer> mCheer;

    public MinionSystem() {
        super(Aspect.all(Minion.class));
    }

    @Override
    protected void process(int e) {

    }

    public Entity spawn() {
        Entity e = new DynastyEntityBuilder(world).with(
                new Bounds(0, 0, 0, 0),
                new Anim("WORKER"))
                .with(Pos.class,Scale.class,Minion.class,
                        Renderable.class).build();
        randomizeLocation(e);
        mScale.get(e).scale = G.ZOOM;
        mRenderable.get(e).layer = MINION_LAYER;
        return e;

    }

    public void allCheer()
    {
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for (int i = 0, s = actives.size(); s > i; i++) {
            int entity = ids[i];
            mCheer.create(entity);
            mSchedule.create(entity).operation.add(
                    OperationFactory.sequence(
                            OperationFactory.delay(MathUtils.random(1f,2f)),
                            OperationFactory.remove(Cheer.class)
                    ));
        }
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
