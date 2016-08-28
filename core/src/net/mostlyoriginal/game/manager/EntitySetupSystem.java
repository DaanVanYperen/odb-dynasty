package net.mostlyoriginal.game.manager;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.camera.Camera;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.manager.AbstractEntityFactorySystem;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.component.resource.Stockpile;
import net.mostlyoriginal.game.component.ui.Button;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.system.dilemma.DilemmaSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;
import net.mostlyoriginal.game.util.Anims;

/**
 * Game specific entity factory.
 *
 * @author Daan van Yperen
 * @todo transform this into a manager.
 */
@Wire
public class EntitySetupSystem extends AbstractEntityFactorySystem {

    public static final int MOUSE_CURSOR_LAYER = 9999;
    private TagManager tagManager;
    private AbstractAssetSystem abstractAssetSystem;
    private DilemmaSystem dilemmaSystem;
    private StockpileSystem stockpileSystem;
    private StructureSystem structureSystem;

    private M<Burrow> mBurrow;
    private M<Pos> mPos;
    private M<Renderable> mRenderable;
    private M<Scale> mScale;

    @Override
    protected void initialize() {
        super.initialize();
        createBackground();
        createLogo();
        createDynastyMetadata();
        initStartingStockpile();
        createMousecursor();
        createCamera(G.CANVAS_WIDTH / 2, G.CANVAS_HEIGHT / 2);
        structureSystem.createPyramid();
        createButton(5, 5, 16 * G.ZOOM, 10 * G.ZOOM, "btn-test", new ButtonListener() {
            @Override
            public void run() {
                dilemmaSystem.startDebugDilemma();
            }
        }, "test");
    }

    private void createBackground() {
        Entity e = new DynastyEntityBuilder(world)
                .with(new Anim("SKY"))
                .with(Pos.class, Renderable.class, Scale.class)
                .build();
        mRenderable.get(e).layer = -100;
        mScale.get(e).scale = G.ZOOM;
        mPos.get(e).xy.y = 133 * G.ZOOM;

        e = new DynastyEntityBuilder(world)
                .with(new Anim("DESERT"))
                .with(Pos.class, Renderable.class, Scale.class)
                .build();
        mRenderable.get(e).layer = 100;
        mScale.get(e).scale = G.ZOOM;
    }

    public void createSkyscrapers() {
        Entity e = new DynastyEntityBuilder(world)
                .with(new Anim("SKYSCRAPERS"))
                .with(Pos.class, Renderable.class, Scale.class, Tint.class)
                .schedule(OperationFactory.tween(new Tint("ffffff00"), new Tint("ffffffff"), 2f))
                .build();
        mRenderable.get(e).layer = -99;
        mScale.get(e).scale = G.ZOOM;
        mPos.get(e).xy.y = 133 * G.ZOOM;
    }

    private void createLogo() {
        float y = G.CANVAS_HEIGHT * 0.75f - (AssetSystem.LOGO_HEIGHT / 2) * G.ZOOM;
        float x = G.CANVAS_WIDTH * 0.5f - (AssetSystem.LOGO_WIDTH / 2) * G.ZOOM;
        Entity e = new DynastyEntityBuilder(world)
                .with(new Anim("LOGO"))
                .schedule(
                        OperationFactory.parallel(
                                OperationFactory.tween(new Pos(x, y - 5 * G.ZOOM), new Pos(x, y + 5 * G.ZOOM), 6f),
                                OperationFactory.sequence(
                                        OperationFactory.tween(new Tint("ffffff00"), new Tint("ffffffff"), 0.5f),
                                        OperationFactory.delay(4),
                                        OperationFactory.tween(new Tint("ffffffff"), new Tint("ffffff00"), 0.5f)
                                )))
                .with(Pos.class, Renderable.class, Scale.class)
                .build();
        mRenderable.get(e).layer = 2000;
        mScale.get(e).scale = G.ZOOM;
        mPos.get(e).xy.y = y;
        mPos.get(e).xy.x = x;
    }


    private void initStartingStockpile() {
        stockpileSystem.alter(StockpileSystem.Resource.COMPLETION, 1);
        stockpileSystem.alter(StockpileSystem.Resource.WORKERS, 5);
        stockpileSystem.alter(StockpileSystem.Resource.CAMELS, 1);

        stockpileSystem.alter(StockpileSystem.Resource.LIFESPAN, 40);
    }

    private void createDynastyMetadata() {
        new DynastyEntityBuilder(world).with(
                new Stockpile()).tag("dynasty").build();
    }

    @Override
    public Entity createEntity(String entity, int cx, int cy, MapProperties properties) {
        return null;
    }


    public void createCamera(int cx, int cy) {
        // now create a drone that will swerve towards the player which contains the camera. this will create a smooth moving camera.
        Entity camera = new DynastyEntityBuilder(world).with(Pos.class).with(createCameraBounds(), new Camera()).build();
        mPos.get(camera).xy.set(cx, cy);
    }

    private Bounds createCameraBounds() {
        // convert viewport into bounds.
        return new Bounds(
                (-Gdx.graphics.getWidth() / 2) / G.ZOOM,
                (-Gdx.graphics.getHeight() / 2) / G.ZOOM,
                (Gdx.graphics.getWidth() / 2) / G.ZOOM,
                (Gdx.graphics.getHeight() / 2) / G.ZOOM
        );
    }

    public Entity createButton(int x, int y, int width, int height, String animPrefix, ButtonListener listener, String hint) {
        Entity e = new DynastyEntityBuilder(world)
                .with(new Bounds(0, 0, width, height),
                        new Anim(),
                        new Button(animPrefix, listener, hint),
                        new Clickable())
                .with(Pos.class, Renderable.class, Tint.class, Scale.class)
                .build();
        mPos.get(e).xy.set(x, y);
        mScale.get(e).scale = G.ZOOM;
        mRenderable.get(e).layer = 1100;
        return e;
    }

    private Entity createMousecursor() {
        Entity entity = new DynastyEntityBuilder(world).with(
                new MouseCursor(),
                new Bounds(0, 0, 0, 0))
                .with(Pos.class)
                .tag("cursor").build();

        return entity;

    }
}
