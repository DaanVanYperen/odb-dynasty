package net.mostlyoriginal.game.component.ui;

import com.artemis.Component;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.game.G;

/**
 * @author Daan van Yperen
 */
public class Label extends Component {

    public String text;
    public Align align = Align.LEFT;
    public Tint shadowColor;

    /** target smokeLayer, higher is in front, lower is behind. */
    public float scale = 1f;
    public float maxWidth = G.CANVAS_WIDTH;

    public Label() {}
    public Label(String text) {
        this.text = text;
    }

    public Label(String text, float scale) {
        this.text = text;
        this.scale = scale;
    }

    public enum Align {
        LEFT, RIGHT;
    }
}
