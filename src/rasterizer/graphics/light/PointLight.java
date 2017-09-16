package rasterizer.graphics.light;

import rasterizer.math.Vector3f;

/**
 * Created by Ryan on 16/09/2017.
 */
public class PointLight extends Light {

    public PointLight(final Vector3f position) {
        super.position.set(position);
    }
}
