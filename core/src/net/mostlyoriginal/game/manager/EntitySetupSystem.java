package net.mostlyoriginal.game.manager;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.manager.AbstractEntityFactorySystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.component.ui.ButtonListener;
import net.mostlyoriginal.game.system.dilemma.DilemmaSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

import static com.artemis.E.E;
import static net.mostlyoriginal.api.operation.JamOperationFactory.moveBetween;
import static net.mostlyoriginal.api.operation.JamOperationFactory.tintBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.*;

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

        /**
         createButton(5, 5, 16 * G.ZOOM, 10 * G.ZOOM, "btn-test", new ButtonListener() {
        @Override public void run() {
        dilemmaSystem.startDebugDilemma();
        }
        }, "test"); **/
    }

    private void createBackground() {
        E()
                .anim("SKY")
                .pos(0, 133 * G.ZOOM)
                .renderable(-100)
                .scale(G.ZOOM)
                .entity();

        E()
                .anim("DESERT")
                .pos()
                .renderable(100)
                .scale(G.ZOOM)
                .entity();
    }

    public void createSkyscrapers() {
        E()
                .anim("SKYSCRAPERS")
                .pos(0, 133 * G.ZOOM)
                .scale(G.ZOOM)
                .renderable(-19)
                .tint()
                .script(
                        tintBetween(Tint.TRANSPARENT, Tint.WHITE, 2f)
                ).entity();
    }

    private void createLogo() {
        float y = G.CANVAS_HEIGHT * 0.75f - (AssetSystem.LOGO_HEIGHT / 2) * G.ZOOM;
        float x = G.CANVAS_WIDTH * 0.5f - (AssetSystem.LOGO_WIDTH / 2) * G.ZOOM;

        E()
                .anim("LOGO")
                .script(
                        parallel(
                                moveBetween(x, y - 5 * G.ZOOM, x, y + 5 * G.ZOOM, 6f),
                                sequence(
                                        tintBetween(Tint.TRANSPARENT, Tint.WHITE, 0.5f),
                                        delay(4),
                                        tintBetween(Tint.WHITE, Tint.TRANSPARENT, 0.5f)
                                )))
                .pos(x, y).renderable(2000).scale(G.ZOOM)
                .entity();
    }


    private void initStartingStockpile() {
        stockpileSystem.alter(StockpileSystem.Resource.COMPLETION, 1);
        stockpileSystem.alter(StockpileSystem.Resource.WORKERS, 5);
        stockpileSystem.alter(StockpileSystem.Resource.CAMELS, 1);

        stockpileSystem.alter(StockpileSystem.Resource.LIFESPAN, 30);
    }

    private void createDynastyMetadata() {
        E().stockpile().tag("dynasty").entity();
    }

    @Override
    public Entity createEntity(String entity, int cx, int cy, MapProperties properties) {
        return null;
    }


    public void createCamera(int cx, int cy) {
        // now create a drone that will swerve towards the player which contains the camera. this will create a smooth moving camera.
        E()
                .pos(cx, cy)
                .bounds(createCameraBounds())
                .pos().camera();
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
        return E()
                .bounds(0, 0, width, height)
                .anim()
                .button(animPrefix, listener, hint)
                .clickable()
                .pos(x, y).renderable(11000).tint().scale(G.ZOOM)
                .entity();
    }

    private Entity createMousecursor() {
        return E()
                .pos()
                .mouseCursor()
                .bounds()
                .tag("cursor").entity();

    }
}
