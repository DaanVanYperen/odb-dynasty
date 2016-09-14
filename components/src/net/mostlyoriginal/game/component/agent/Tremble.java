package net.mostlyoriginal.game.component.agent;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;

/**
 * Created by Daan on 27-8-2016.
 */
@DelayedComponentRemoval
public class Tremble extends Component {

    public Tremble() {}

    public float appliedX;
    public float age;
    public float intensity = 1;
}
