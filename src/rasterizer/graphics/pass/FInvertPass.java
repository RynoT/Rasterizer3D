package rasterizer.graphics.pass;

/**
 * Created by Ryan on 17/09/2017.
 */
public class FInvertPass implements FragmentPass {

    @Override
    public boolean pass(final PassParameters params) {
        params.foutColor[0] = 1.0f - params.foutColor[0];
        params.foutColor[1] = 1.0f - params.foutColor[1];
        params.foutColor[2] = 1.0f - params.foutColor[2];
        return true;
    }
}
