package net.mostlyoriginal.game.system.agent;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.operation.temporal.TweenOperation;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.agent.Burrow;
import net.mostlyoriginal.game.component.agent.Tremble;
import net.mostlyoriginal.game.manager.AssetSystem;

/**
 * Created by Daan on 27-8-2016.
 */
public class BurrowSystem extends EntityProcessingSystem {

    protected M<Anim> mAnim;
    protected M<Pos> mPos;
    protected M<Burrow> mBurrow;
    protected M<Tremble> mTremble;

    protected AssetSystem assetSystem;

    public BurrowSystem() {
        super(Aspect.all(Burrow.class, Pos.class));
    }

    @Override
    protected void process(Entity e) {

        Pos pos = mPos.get(e);
        Burrow burrow = mBurrow.get(e);
        Anim anim = mAnim.get(e);

        Animation animation = assetSystem.get(anim.id);

        if (Math.abs(burrow.targetPercentage - burrow.percentage) >= 0.01f) {
            float distance = Math.abs(burrow.targetPercentage - burrow.percentage);
            float delta = MathUtils.clamp(burrow.targetPercentage - burrow.percentage, -0.01f, 0.01f) * world.getDelta() * burrow.speed;
            if (distance < 0.05f) {
                burrow.percentage += delta * 0.5f;
                startTrembling(e, 0.5f);
            } else {
                burrow.percentage += delta;
                startTrembling(e, 1);
            }


            pos.xy.y = burrow.surfaceY - (animation.getKeyFrame(0).getRegionHeight() *
                    burrow.percentage);

        } else {
            stopTrembling(e);
        }
    }

    private void stopTrembling(Entity e) {
        if (mTremble.has(e)) {
            mTremble.remove(e);
        }
    }

    private void startTrembling(Entity e, float intensity) {
        mTremble.create(e).intensity = intensity;
    }
}
