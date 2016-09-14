package net.mostlyoriginal.game.component.ui;

import com.artemis.Component;
import com.artemis.annotations.Fluid;
import net.mostlyoriginal.api.component.graphics.Tint;

/**
 * @author Daan van Yperen
 */
@Fluid( name = "localLabel")
public class Label extends Component {

    public String text;
    public Align align = Align.LEFT;
    public Tint shadowColor;

    /** target smokeLayer, higher is in front, lower is behind. */
    public float scale = 1f;
    public float maxWidth = 160 * 3;

    public Label() {}
    public Label(String text) {
        this.text = text;
    }

    public Label(String text, float scale) {
        this.text = text;
        this.scale = scale;
    }

    public void set(String text, float scale) {
        this.text = text;
        this.scale = scale;
    }

    public void set(String text, Align align) {
        this.text = text;
        this.align = align;
    }

    public void set(String text) {
        this.text = text;
    }

    public enum Align {
        LEFT, RIGHT;
    }
}
