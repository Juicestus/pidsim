package components;

import java.awt.geom.AffineTransform;
import java.awt.*;

public abstract class TorqueShape {
    public int cx = 0;
    public int cy = 0;
    public int w = 0;
    public int h = 0;
    public double rot = 0;

    public TorqueShape(int cx, int cy, int w, int h) {
        this.cx = cx;
        this.cy = cx;
        this.w = w;
        this.h = h;
    }

    public TorqueShape(int cx, int cy, int w, int h, double rot) {
        this.cx = cx;
        this.cy = cx;
        this.w = w;
        this.h = h;
        this.rot = rot;
    }

    public void clampRotation(int low, int high) {
        rot = MathUtils.clamp(rot, low, high);
    }

    protected Shape applyEffects(Shape shape) {
        AffineTransform rotation = new AffineTransform();
        rotation.rotate(Math.toRadians(rot));

        AffineTransform translation = new AffineTransform();
        translation.translate(cx, cy);

        shape = rotation.createTransformedShape(shape);
        shape = translation.createTransformedShape(shape);
        return shape;
    }

    public abstract Shape getShape();
}