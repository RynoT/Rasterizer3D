package rasterizer.graphics.pass;

/**
 * Created by Ryan on 17/09/2017.
 */
public class FGrayscalePass implements FragmentPass {

    @Override
    public boolean pass(final PassParameters params) {
        final float rgb = (params.foutColor[0] + params.foutColor[1] + params.foutColor[2]) / 3.0f;
        params.foutColor[0] = params.foutColor[1] = params.foutColor[2] = rgb;
        return true;
    }
}
