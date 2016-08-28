package net.mostlyoriginal.game.component.agent;

import com.artemis.Component;

/**
 * Created by Daan on 27-8-2016.
 */
public class Burrow extends Component {

    public Burrow() {
    }

    public int surfaceY;
    public float smokeAge;
    public int smokeLayer = 1;
    public float speed = 80;
    public float percentage = 0;
    public float targetPercentage = 1;

    public float topWidthPercentage = 0;
}
