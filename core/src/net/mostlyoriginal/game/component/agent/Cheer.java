package net.mostlyoriginal.game.component.agent;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Daan on 27-8-2016.
 */
@DelayedComponentRemoval
public class Cheer extends Component {

    public Cheer() {}

    public float appliedY;
    public float age = MathUtils.random(1000);
    public float intensity = 4;
}
