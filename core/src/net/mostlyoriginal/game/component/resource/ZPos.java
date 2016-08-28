package net.mostlyoriginal.game.component.resource;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Daan on 28-8-2016.
 */
public class ZPos extends Component {
    public float z = MathUtils.random(48);
    public ZPos() {
    }

    public ZPos(float z) {
        this.z = z;
    }
}
