package net.mostlyoriginal.game.system.render;

/**
 * @author Daan van Yperen
 */

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.systems.FluidDeferredEntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.delegate.DeferredEntityProcessingSystem;
import net.mostlyoriginal.api.system.delegate.EntityProcessPrincipal;
import net.mostlyoriginal.game.component.ui.Label;
import net.mostlyoriginal.game.manager.FontManager;

/**
 * Render and progress animations.
 *
 * @author Daan van Yperen
 * @see net.mostlyoriginal.api.component.graphics.Anim
 */
@Wire
public class LabelRenderSystem extends FluidDeferredEntityProcessingSystem {

    protected CameraSystem cameraSystem;
    protected FontManager fontManager;

    protected SpriteBatch batch;
    private GlyphLayout glyphLayout;

    public LabelRenderSystem(EntityProcessPrincipal principal) {
        super(Aspect.all(Pos.class, Label.class, Renderable.class).exclude(Invisible.class), principal);
        batch = new SpriteBatch(1000);
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    protected void initialize() {
        super.initialize();
        glyphLayout = new GlyphLayout();
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
    protected void process(E e) {

        final Label label = e._localLabel();
        final Pos pos = e._pos();

        if (label.text != null) {

            final BitmapFont font = label.scale == 3f ? fontManager.bigFont : fontManager.font;

            glyphLayout.setText(font, label.text);

            // shadow, if any.
            if (label.shadowColor != null) {
                final Color color = label.shadowColor.color;
                font.setColor(color.r, color.g, color.b, color.a);
                switch (label.align) {
                    case LEFT:
                        font.draw(batch, label.text, pos.xy.x - 2, pos.xy.y + 2, label.maxWidth, Align.topLeft, true);
                        font.draw(batch, label.text, pos.xy.x + 2, pos.xy.y - 2, label.maxWidth, Align.topLeft, true);
                        break;
                    case RIGHT:
                        font.draw(batch, label.text, pos.xy.x - glyphLayout.width + 2, pos.xy.y - 2);
                        font.draw(batch, label.text, pos.xy.x - glyphLayout.width - 2, pos.xy.y + 2);
                        break;
                }
            }

            if (e.hasTint()) {
                final Color color = e.tintColor();
                font.setColor(color.r, color.g, color.b, color.a);
            } else {
                font.setColor(1f, 1f, 1f, 1f);
            }
            switch (label.align) {
                case LEFT:
                    font.draw(batch, label.text, pos.xy.x, pos.xy.y, label.maxWidth, Align.topLeft, true);
                    break;
                case RIGHT:
                    font.draw(batch, label.text, pos.xy.x - glyphLayout.width, pos.xy.y);
                    break;
            }
        }
    }

    public float estimateHeight(Label label) {
        final BitmapFont font = label.scale == 3f ? fontManager.bigFont : fontManager.font;
        return new GlyphLayout(font, label.text,new Color(),label.maxWidth, Align.topLeft, true).height;
    }
}
