package rasterizer.graphics.pass;

import rasterizer.math.MathUtils;

/**
 * Created by Ryan on 14/09/2017.
 */
public class FTexturePass extends FragmentPass {

    @Override
    public boolean pass(final PassParameters params) {
        if(!params.inHasTexture || params.finMaterial == null) {
            return false;
        }
        final float[] pixels = params.finMaterial.getPixels();
        final int index = params.finMaterial.getIndex(params.finTexture[0], params.finTexture[1]);

        final float maxZ = 2.0f, minZ = 1.0f;
        final float mul = 1.0f - MathUtils.max(0.0f, MathUtils.min(1.0f, (params.finPoint[2] - minZ) / (maxZ - minZ)));

        params.foutColor[0] = pixels[index] * mul;
        params.foutColor[1] = pixels[index + 1] * mul;
        params.foutColor[2] = pixels[index + 2] * mul;
        params.foutColor[3] = params.finMaterial.hasAlpha() ? pixels[index + 3] : 1.0f;

        return true;
    }
}
