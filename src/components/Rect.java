package components;

import java.awt.*;

public class Rect extends TorqueShape {

    public Rect(int cx, int cy, int w, int h) {
        super(cx, cy, w, h);
    }

    public Shape getShape() {
        Rectangle rect = new Rectangle(-w / 2, -h / 2, w, h);
        return applyEffects(rect);
    }
}