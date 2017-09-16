package rasterizer.graphics.pass;

import java.awt.*;

/**
 * Created by Ryan on 14/09/2017.
 */
public class FSolidColorPass extends FragmentPass {

    private final float[] rgba;

    public FSolidColorPass(final Color color) {
        this(new float[]{color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f});
    }

    public FSolidColorPass(final float[] rgba) {
        assert rgba != null && rgba.length == 4;
        this.rgba = rgba;
    }

    @Override
    public boolean pass(final PassParameters params) {
        final float maxZ = 2.0f, minZ = 1.0f;
        final float mul = params._skip_lighting ? 1.0f : 1.0f - Math.max(0.0f, Math.min(1.0f, (params.finPoint[2] - minZ) / (maxZ - minZ)));

        params.foutColor[0] = this.rgba[0] * mul;
        params.foutColor[1] = this.rgba[1] * mul;
        params.foutColor[2] = this.rgba[2] * mul;
        params.foutColor[3] = this.rgba[3];

        return true;
    }
}
