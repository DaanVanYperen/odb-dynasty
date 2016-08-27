package net.mostlyoriginal.game.manager;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.camera.Camera;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.mouse.MouseCursor;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.manager.AbstractEntityFactorySystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
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

    private M<Burrow> mBurrow;
    private M<Pos> mPos;
    private M<Renderable> mRenderable;

    @Override
    protected void initialize() {
        super.initialize();
        createDynastyMetadata();
        initStartingStockpile();
        createMousecursor();
        createCamera(G.CANVAS_WIDTH / 2, G.CANVAS_HEIGHT / 2);
        createPyramid();
        createButton( 5, 5, 50, 10, "btn-scan", new ButtonListener(){
            @Override
            public void run() {
                dilemmaSystem.randomDilemma();
            }
        }, "test");
    }

    private void createPyramid() {
        createStructure(G.CANVAS_WIDTH / 2, G.CANVAS_HEIGHT / 2, "dancingman");
    }

    private void createStructure(int x, int y, String dancingman) {
        Entity entity = Anims.createCenteredAt(world,
                AssetSystem.DANCING_MAN_WIDTH,
                AssetSystem.DANCING_MAN_HEIGHT,
                "dancingman",
                Anims.scaleToScreenRoundedHeight(0.3f, AssetSystem.DANCING_MAN_HEIGHT));
        mPos.get(entity).xy.set(x,y);

        Burrow burrow = mBurrow.create(entity);
        burrow.percentage=1.0f;
        burrow.targetPercentage=0.0f;
        burrow.surfaceY=y;
    }

    private void initStartingStockpile() {
        stockpileSystem.alter(StockpileSystem.Resource.WORKERS, 5);
    }

    private void createDynastyMetadata() {
        new EntityBuilder(world).with(
                new Stockpile()).tag("dynasty").build();
    }

    @Override
    public Entity createEntity(String entity, int cx, int cy, MapProperties properties) {
        return null;
    }


    public void createCamera(int cx, int cy) {
        // now create a drone that will swerve towards the player which contains the camera. this will create a smooth moving camera.
        Entity camera = new EntityBuilder(world).with(Pos.class).with(createCameraBounds(), new Camera()).build();
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
        Entity entity = new EntityBuilder(world)
                .with(new Bounds(0, 0, width, height),
                        new Anim(),
                        new Button(animPrefix, listener, hint),
                        new Clickable())
                .with(Pos.class, Renderable.class, Tint.class)
                .build();
        mPos.get(entity).xy.set(x, y);
        mRenderable.get(entity).layer = 1100;
        return entity;
    }

    private Entity createMousecursor() {
        Entity entity = new EntityBuilder(world).with(
                new MouseCursor(),
                new Bounds(0, 0, 0, 0),
                new Anim("dancingman"))
                .with(Pos.class,
                        Renderable.class)
                .tag("cursor").build();

        mRenderable.get(entity).layer = MOUSE_CURSOR_LAYER;
        return entity;

    }
}
