package rasterizer.graphics.layer;

import rasterizer.graphics.target.RenderTarget;

import java.awt.*;

/**
 * Created by Ryan on 12/09/2017.
 */
public abstract class Layer extends RenderTarget {

    public Layer(final int width, final int height) {
        super(width, height);
    }

    public abstract void render();
}
