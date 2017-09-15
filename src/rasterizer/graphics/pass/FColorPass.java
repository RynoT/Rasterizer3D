package rasterizer.graphics.pass;

import java.awt.*;

/**
 * Created by Ryan on 14/09/2017.
 */
public class FColorPass extends FragmentPass {

    private final float[] rgba;

    public FColorPass(final Color color) {
        this(new float[]{color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f});
    }

    public FColorPass(final float[] rgba) {
        assert rgba != null && rgba.length == 4;
        this.rgba = rgba;
    }

    @Override
    public boolean pass(final float[] outRGBA) {
        System.arraycopy(this.rgba, 0, outRGBA, 0, outRGBA.length);
        return true;
    }
}
