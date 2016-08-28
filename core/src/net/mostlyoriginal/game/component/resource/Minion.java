package net.mostlyoriginal.game.component.resource;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Daan on 27-8-2016.
 */
public class Minion extends Component {

    public int productivity=1;
    public float z = MathUtils.random(48);

    public Minion() {
    }

    public Minion(int productivity) {
        this.productivity = productivity;
    }
}
