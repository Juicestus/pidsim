package components;

import java.awt.geom.AffineTransform;
import java.awt.*;

public class Rect {
    public double rot = 0;
    public int cx = 0;
    public int cy = 0;
    public int w = 0;
    public int h = 0;

    public Rect(int cx, int cy, int w, int h) {
        this.cx = cx;
        this.cy = cx;
        this.w = w;
        this.h = h;
    }

    public Shape getShape() {
        AffineTransform rotation = new AffineTransform();
        rotation.rotate(Math.toRadians(rot));

        AffineTransform translation = new AffineTransform();
        translation.translate(cx, cy);

        Rectangle rect = new Rectangle(-(int) (w / 2), -(int) (h / 2), w, h);
        Shape shape = rotation.createTransformedShape(rect);
        shape = translation.createTransformedShape(shape);
        return shape;
    }

    public void clampBetween(int lower, int upper) {
        if (rot > upper) {
            rot = upper;
        }
        if (rot < lower) {
            rot = lower;
        }
    }
}