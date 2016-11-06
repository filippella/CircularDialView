package org.dalol.oldcirculardialview;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Filippo on 8/7/2016.
 */
public class WheelView {

    private float x, y, radius;

    public WheelView(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
}
