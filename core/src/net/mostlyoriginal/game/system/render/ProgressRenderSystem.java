package net.mostlyoriginal.game.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.ui.Bar;
import net.mostlyoriginal.game.component.ui.Progress;
import net.mostlyoriginal.game.manager.FontManager;

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

    @Override
    protected void initialize() {
        super.initialize();
        glyphLayout = new GlyphLayout();
    }

    public ProgressRenderSystem(EntityProcessPrincipal principal) {
        super(Aspect.all(Pos.class, Progress.class).exclude(Invisible.class), principal);
        batch = new SpriteBatch(100);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.5f);
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

        G.ZOOM = 1;

        final Progress progress = mProgress.get(entity);
        final Pos pos = pm.get(entity);

        TextureRegion barBackground = abstractAssetSystem.get("PROGRESS BAR BACKGROUND").getKeyFrame(0, false);
        TextureRegion bar = abstractAssetSystem.get("PROGRESS BAR").getKeyFrame(0, false);
        TextureRegion barPlanned = abstractAssetSystem.get("PROGRESS BAR PLANNED").getKeyFrame(0, false);
        if (progress == null) return;

        int barBackgroundX = 7 * G.ZOOM;
        int barBackgroundY = G.CANVAS_HEIGHT - 7 * G.ZOOM - barBackground.getRegionHeight() * G.ZOOM;

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

        G.ZOOM = 3;
    }

}
