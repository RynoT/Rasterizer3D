package rasterizer.graphics.layer;

import java.awt.*;

/**
 * Created by Ryan on 12/09/2017.
 */
public class Layer2D extends Layer {

    public Layer2D(final int width, final int height) {
        super(width, height);

        // Stop RGBA RenderTarget data from writing to our layer
        super.disableCustomRGBA = true;
    }

    @Override
    public void render() {
        final Graphics2D g2d = super.createGraphics();

        g2d.dispose();
    }
}
