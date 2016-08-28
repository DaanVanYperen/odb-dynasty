package net.mostlyoriginal.game.component.ui;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Label extends Component {

    public String text;
    public Align align = Align.LEFT;

    /** target smokeLayer, higher is in front, lower is behind. */
    public float scale = 1f;

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
