package net.mostlyoriginal.game.component.resource;

import com.artemis.Component;

/**
 * Created by Daan on 27-8-2016.
 */
public class Fireball extends Component {

    public float smokeCooldown;
    public float sparkCooldown;
    public boolean hasSmoke;
    public boolean hasSparks;
    public boolean explodes;

    public Fireball() {
    }

    public void set(boolean smoke, boolean spark, boolean explodes) {
        this.hasSmoke = smoke;
        this.hasSparks = spark;
        this.explodes = explodes;
    }

}
