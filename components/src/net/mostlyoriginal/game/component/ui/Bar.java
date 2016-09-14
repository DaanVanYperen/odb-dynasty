package net.mostlyoriginal.game.component.ui;

import com.artemis.annotations.Fluid;

/**
 * @author Daan van Yperen
 */
@Fluid( name = "bar")
public class Bar extends Label {

    public int value;
    public String animationIdEmpty;
    public int valueEmpty;
    public String animationId;

    public Bar() {}
    public Bar(String text, String animationId, int value, String animationIdEmpty, int valueEmpty) {
        super(text);
        this.animationId = animationId;
        this.value = value;
        this.animationIdEmpty = animationIdEmpty;
        this.valueEmpty = valueEmpty;
    }
}
