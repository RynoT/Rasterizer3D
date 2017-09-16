package rasterizer.graphics.light;

import rasterizer.graphics.target.RenderTarget;
import rasterizer.math.Vector3f;

/**
 * Created by Ryan on 16/09/2017.
 */
public abstract class Light {

    protected final Vector3f position = new Vector3f();
    protected final float[] color = new float[RenderTarget.RGBA_FLOAT_LENGTH];

    protected Light() {
    }

    public float[] getColor() {
        return this.color;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Light setColor(final float[] color) {
        assert color != null && color.length == this.color.length;
        System.arraycopy(color, 0, this.color, 0, color.length);
        return this;
    }

    public Light setPosition(final Vector3f position) {
        assert position != null;
        this.position.set(position);
        return this;
    }
}
