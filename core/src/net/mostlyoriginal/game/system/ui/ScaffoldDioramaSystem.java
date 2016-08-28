package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.EntitySubscription;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Scaffold;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.manager.SmokeSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

import static net.mostlyoriginal.game.system.ui.RiverDioramaSystem.RiverState.*;

/**
 * Minions need scaffolds!
 * <p>
 * Created by Daan on 27-8-2016.
 */
public class ScaffoldDioramaSystem extends BaseSystem {

    public static final int BIG_SCAFFOLD_HEIGHT = 16;
    private static final int BIG_SCAFFOLD_WIDTH = 16;
    public static final int MAX_COLUMNS = 12;

    protected M<Pos> mPos;

    protected SmokeSystem smokeSystem;
    private int targetHeight[] = new int[MAX_COLUMNS];
    private int actualHeight[] = new int[MAX_COLUMNS];

    private static final String[] bigScaffolds = {"SCAFFOLDING BIG 1", "SCAFFOLDING BIG 2", "SCAFFOLDING BIG 3"};
    private static final String[] smallScaffolds = {"SCAFFOLDING SMALL TOP 1", "SCAFFOLDING SMALL TOP 1", "SCAFFOLDING SMALL TOP 1"};
    private static final String[] items = {
            "SCAFFOLDING OBJECTS 1",
            "SCAFFOLDING OBJECTS 2",
            "SCAFFOLDING OBJECTS 3",
            "SCAFFOLDING OBJECTS 4",
            "SCAFFOLDING OBJECTS 5",
            "SCAFFOLDING OBJECTS 6",
            "SCAFFOLDING OBJECTS 7",
            "SCAFFOLDING OBJECTS 8",
            "SCAFFOLDING OBJECTS 9",
            "SCAFFOLDING OBJECTS 10",
            "SCAFFOLDING FLAG",
            "SCAFFOLDING BACKGROUND SMALL 1",
            "SCAFFOLDING SMALL TILE 1"

    };
    private AssetSystem assetSystem;
    private EntitySubscription scaffoldSubscription;

    /*
    add("SCAFFOLDING SMALL TILE 1", 488, 224, 8,  8,1);
    add("SCAFFOLDING BACKGROUND SMALL 1", 496, 224, 8,  8,1);
    add("SCAFFOLDING FLAG", 496, 216, 8,  8,1);
    add("SCAFFOLDING OBJECTS 1", 504, 216, 8,  8,1);
    add("SCAFFOLDING OBJECTS 2", 512, 216, 8,  8,1);
    add("SCAFFOLDING OBJECTS 3", 520, 216, 8,  8,1);
    add("SCAFFOLDING OBJECTS 4", 528, 216, 8,  8,1);
    add("SCAFFOLDING OBJECTS 5", 536 , 216, 8,  8,1);
    add("SCAFFOLDING OBJECTS 6", 504, 224, 8,  8,1);
    add("SCAFFOLDING OBJECTS 7", 512, 224, 8,  8,1);
    add("SCAFFOLDING OBJECTS 8", 520, 224, 8,  8,1);
    add("SCAFFOLDING OBJECTS 9", 528, 224, 8,  8,1);
    add("SCAFFOLDING OBJECTS 10", 536, 224, 8,  8,1);*/

    @Override
    protected void initialize() {
        scaffoldSubscription = world.getAspectSubscriptionManager().get(Aspect.all(Scaffold.class));
    }

    protected void spawn(int x, int y, int height) {
        int bigHeight = height / 2;

        int yy = y;

        for (int i = 0; i < bigHeight; i++) {
            createScaffold(x, yy, bigScaffolds);
            yy += BIG_SCAFFOLD_HEIGHT * G.ZOOM;
            if (MathUtils.random(0,100)<75) {
                createScaffold(x + MathUtils.random(0,BIG_SCAFFOLD_WIDTH/2-1) * G.ZOOM, yy, items);
            }
        }

        if (height % 2 == 0) {
            int smallX = x + MathUtils.random(0, BIG_SCAFFOLD_WIDTH / 2 - 1) * G.ZOOM;
            createScaffold(smallX, yy, smallScaffolds);
            yy += BIG_SCAFFOLD_HEIGHT / 2 * G.ZOOM;
            if (MathUtils.random(0,100)<75) {
                createScaffold(smallX, yy, items);
            }
        }
    }

    private void createScaffold(int x, int y, String[] ids) {
        String id = ids[MathUtils.random(0, ids.length - 1)];
        new DynastyEntityBuilder(world)
                .pos(x, y)
                .with(Scaffold.class)
                .renderable(500)
                .anim(id)
                .scale(G.ZOOM).build();

        TextureRegion frame = assetSystem.get(id).getKeyFrame(0,true);
        smokeSystem.cloud(x,y,x+frame.getRegionWidth()*G.ZOOM,y+frame.getRegionHeight()*G.ZOOM, 20, 505);
    }

    float age;

    @Override
    protected void processSystem() {

        age += world.delta;
        if ( age >= 1 ) {
            age-=1;
            tick();
        }
    }

    private void tick() {
        for (int column = 0; column < MAX_COLUMNS; column++) {

            if ( targetHeight[column] != actualHeight[column] && MathUtils.random(0,100) < 25 ) {
                int x = column * BIG_SCAFFOLD_WIDTH * G.ZOOM;

                expireScaffolds(x, x+((BIG_SCAFFOLD_WIDTH-1)*G.ZOOM));

                actualHeight[column] +=
                        MathUtils.clamp(targetHeight[column] - actualHeight[column], -3, 1);

                if ( actualHeight[column] > 0 ) {
                    spawn(x, 133 * G.ZOOM - 4 * G.ZOOM, actualHeight[column]);
                }
            }
        }
    }

    private void expireScaffolds(int x, int x2) {
        IntBag entities = scaffoldSubscription.getEntities();
        for(int i=0,s=entities.size();i<s;i++)
        {
            int entity = entities.get(i);
            float entityX = mPos.get(entity).xy.x;
            if ( entityX >= x && entityX <= x2 )
            {
                world.delete(entity);
            }
        }
    }

    public void kill()
    {
        for(int column=0;column<MAX_COLUMNS;column++) {
            targetHeight[column]=0;
        }

    }

    public void stack(int col, int lastCol, float stacks) {
        for(int column=col;column<=lastCol;column++)
        {
            targetHeight[column] =
                    MathUtils.random(0, 100) < 10 ? (int) MathUtils.random(stacks*0.25f,stacks) : (int) stacks;
            if ( targetHeight[column] < 0 ) targetHeight[column] = 0;
        }
    }
}
