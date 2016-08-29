package net.mostlyoriginal.game.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.ui.*;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.manager.FontManager;
import net.mostlyoriginal.game.system.logic.ProgressAlgorithmSystem;

import static net.mostlyoriginal.game.system.dilemma.DilemmaSystem.COLOR_RAW_BRIGHT;
import static net.mostlyoriginal.game.system.dilemma.DilemmaSystem.COLOR_RAW_DIMMED;
import static net.mostlyoriginal.game.system.dilemma.DilemmaSystem.TEXT_ZOOM;

/**
 * Render and progress animations.
 *
 * @author Daan van Yperen
 * @see net.mostlyoriginal.api.component.graphics.Anim
 */
@Wire
public class ProgressRenderSystem extends DeferredEntityProcessingSystem {

    private ComponentMapper<Pos> pm;
    protected ComponentMapper<Tint> mTint;
    private ComponentMapper<Progress> mProgress;

    private AbstractAssetSystem abstractAssetSystem;
    private CameraSystem cameraSystem;
    private FontManager fontManager;

    private SpriteBatch batch;
    private GlyphLayout glyphLayout;
    private float age;
    private ProgressAlgorithmSystem progressAlgorithmSystem;
    private Entity progressButton;
    private M<Invisible> mInvisible;
    private AssetSystem assetSystem;
    private Entity buildLabel;


    public void createLabel(int x, int y, String color, String text, String shadowTextColor, int maxWidth) {
        Label label = new Label(text, TEXT_ZOOM);
        label.shadowColor = new Tint(shadowTextColor);
        label.maxWidth = maxWidth;
        int insertDistanceY =AssetSystem.SLAB_HEIGHT*G.ZOOM;
        DynastyEntityBuilder builder = new DynastyEntityBuilder(world)
                .with(label)
                .pos(x, y)
                .renderable(3010)
                .scale(TEXT_ZOOM)
                .tint(color);

        buildLabel = builder
                .build();
    }

    @Override
    protected void initialize() {
        super.initialize();
        glyphLayout = new GlyphLayout();

        createLabel(12 * G.ZOOM, 4 * G.ZOOM + 18 * G.ZOOM, "FFFFFFFF", "Build progress", "000000FF", 4000);
        progressButton = new DynastyEntityBuilder(world)
                .with(Tint.class).with(
                new Bounds(0, 0, 26*G.ZOOM, 16*G.ZOOM),
                new Clickable(),
                new Button("btn-turn-up", "btn-turn-hover", "btn-turn-down", new ButtonListener() {
                    @Override
                    public void run() {
                        assetSystem.playRandomHammer();
                        assetSystem.playRandomHammer();
                        progressAlgorithmSystem.progress();
                    }
                })
        )
                .group("progress")
                .anim("btn-test-up")
                .renderable(920)
                .pos(7 * G.ZOOM + AssetSystem.PROGRESS_BAR_BACKGROUND_WIDTH * G.ZOOM, 4 * G.ZOOM)
                .scale(G.ZOOM)
                .build();

    }

    public ProgressRenderSystem(EntityProcessPrincipal principal) {
        super(Aspect.all(Pos.class, Progress.class).exclude(Invisible.class), principal);
        batch = new SpriteBatch(100);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.7f);
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    @Override
    protected void process(int entity) {

        age += world.delta;

        if ( progressAlgorithmSystem.isReadyToProgress() )
        {
            renderBars(entity);
        }

        if ( progressAlgorithmSystem.isReadyToProgress() && !progressAlgorithmSystem.tallying )
        {
            mInvisible.remove(progressButton);
            mInvisible.remove(buildLabel);
        } else {
            mInvisible.create(progressButton);
            mInvisible.create(buildLabel);
        }

    }

    private void renderBars(int entity) {
        final Progress progress = mProgress.get(entity);
        final Pos pos = pm.get(entity);

        TextureRegion barBackground = abstractAssetSystem.get("PROGRESS BAR BACKGROUND").getKeyFrame(0, false);
        TextureRegion bar = abstractAssetSystem.get("PROGRESS BAR").getKeyFrame(0, false);
        TextureRegion barPlanned = abstractAssetSystem.get("PROGRESS BAR PLANNED").getKeyFrame(0, false);
        if (progress == null) return;

        int barBackgroundX = 7 * G.ZOOM;
        int barBackgroundY = 4 * G.ZOOM;

        batch.draw(barBackground,
                barBackgroundX,
                barBackgroundY,
                barBackground.getRegionWidth() * G.ZOOM,
                barBackground.getRegionHeight() * G.ZOOM);

        int maxBarWidth = barBackground.getRegionWidth() - 12;
        float filledWidth = maxBarWidth * progress.value;

        int barX = 13 * G.ZOOM;
        int barY = barBackgroundY + 4 * G.ZOOM;

        if ( progress.value > 0 ) {

            batch.draw(bar,
                    barX,
                    barY,
                    filledWidth * G.ZOOM,
                    bar.getRegionHeight() * G.ZOOM);
        }

        if ( progress.planned > 0 && filledWidth < maxBarWidth )
        {
            int filledPlannedWidth = (int) MathUtils.clamp(maxBarWidth * progress.planned,0f,(float)(maxBarWidth-filledWidth));

            barX += filledWidth * G.ZOOM;
            batch.setColor(1f,1f,1f,0.9f + (0.1f * MathUtils.sinDeg(age * 200f)));
            batch.draw(barPlanned,
                    barX,
                    barY,
                    filledPlannedWidth * G.ZOOM,
                    barPlanned.getRegionHeight() * G.ZOOM);
            batch.setColor(1f,1f,1f,1f);
        }
    }

}
