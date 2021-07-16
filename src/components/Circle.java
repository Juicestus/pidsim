package components;

import java.awt.*;
import java.awt.geom.*;

public class Circle extends TorqueShape {

    public Circle(int cx, int cy, int w, int h) {
        super(cx, cy, w, h);
    }

    @Override
    public Shape getShape() {

        Ellipse2D.Double circle = new Ellipse2D.Double(-(int) (w / 2), -(int) (h / 2), w, h);
        return applyEffects(circle);
    }

}
