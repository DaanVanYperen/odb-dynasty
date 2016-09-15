package net.mostlyoriginal.game.system.agent;

import com.artemis.Aspect;
import com.artemis.E;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.artemis.systems.FluidIteratingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.manager.SmokeSystem;

/**
 * Created by Daan on 27-8-2016.
 */
public class BurrowSystem extends FluidIteratingSystem {

    protected AssetSystem assetSystem;
    protected SmokeSystem smokeSystem;

    public BurrowSystem() {
        super(Aspect.all(Burrow.class, Pos.class));
    }

    @Override
    protected void process(E e) {

        Burrow burrow = e._burrow();

        Animation animation = assetSystem.get(e.animId());

        if (Math.abs(burrow.targetPercentage - burrow.percentage) >= 0.01f) {
            float distance = Math.abs(burrow.targetPercentage - burrow.percentage);
            float delta = MathUtils.clamp(burrow.targetPercentage - burrow.percentage, -0.01f, 0.01f) * world.getDelta() * burrow.speed;
            if (distance < 0.05f) {
                burrow.percentage += delta * 0.5f;
                burrow.smokeAge += world.delta * 0.5f;
                startTrembling(e, 0.5f);
            } else {
                burrow.smokeAge += world.delta;
                burrow.percentage += delta;
                startTrembling(e, 1);
            }

            float scale = e.scaleScale();

            TextureRegion keyFrame = animation.getKeyFrame(0);

            e.posY(burrow.surfaceY - (keyFrame.getRegionHeight() * scale *  burrow.percentage));

            if (burrow.smokeAge > 1f / 30f) {
                burrow.smokeAge -= 1f / 30f;
                float maxWidth = keyFrame.getRegionWidth() * scale;
                // some structures are wider at the bottom. account for this with the dust.
                float surfacedWidth = Interpolation.linear.apply(maxWidth, maxWidth * burrow.topWidthPercentage, burrow.percentage);
                smokeSystem.dust(burrow.surfaceY, e.posX() + maxWidth / 2f - surfacedWidth / 2f, e.posX() + maxWidth / 2f + surfacedWidth / 2f, 5, burrow.smokeLayer);
            }


        } else {
            stopTrembling(e);
        }
    }

    private void stopTrembling(E e) {
        e.removeTremble();
    }

    private void startTrembling(E e, float intensity) {
        e.trembleIntensity(intensity);
    }
}
