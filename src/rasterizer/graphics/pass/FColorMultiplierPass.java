package rasterizer.graphics.pass;

/**
 * Created by Ryan on 17/09/2017.
 */
public class FColorMultiplierPass implements FragmentPass {

    private final float[] rgb;

    public FColorMultiplierPass(final float r, final float g, final float b) {
        this(new float[]{ r, g, b });
    }

    public FColorMultiplierPass(final float[] rgb) {
        assert rgb != null && rgb.length == 3;
        this.rgb = rgb;
    }

    @Override
    public boolean pass(final PassParameters params) {
        params.foutColor[0] *= this.rgb[0];
        params.foutColor[1] *= this.rgb[1];
        params.foutColor[2] *= this.rgb[2];
        return true;
    }
}
