package net.mostlyoriginal.api.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.component.graphics.InterpolationStrategy;
import net.mostlyoriginal.api.component.graphics.Tint;

/**
 * Temporary help methods to deal with libgdx->generic transition in artemis-odb-contrib.
 *
 * @todo get rid of this.
 * @author Daan van Yperen
 */
public class GdxUtil {

    public static Tint convert(Color source) {
        return new Tint(source.r, source.g, source.b, source.a);
    }

    public static InterpolationStrategy convert(final Interpolation interpolation) {
        return new InterpolationStrategy() {
            @Override
            public float apply(float v1, float v2, float a) {
                return interpolation.apply(v1,v2,a);
            }
        };
    }

    public static Tint asColor(String hex) {
        return convert(Color.valueOf(hex));
    }
}
