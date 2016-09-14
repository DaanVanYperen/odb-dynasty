package net.mostlyoriginal.game.component.resource;

import com.artemis.Component;
import com.artemis.annotations.Fluid;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Daan on 28-8-2016.
 */
public class ZPos extends Component {
    public float z = MathUtils.random(48);
    public int layerOffset=0;
    public float height=0;

    public ZPos() {
    }

    public void set(float z) {
        this.z = z;
    }
}
