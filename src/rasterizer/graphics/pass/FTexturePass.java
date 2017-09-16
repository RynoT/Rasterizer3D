package rasterizer.graphics.pass;

/**
 * Created by Ryan on 14/09/2017.
 */
public class FTexturePass extends FragmentPass {

    @Override
    public boolean pass(final PassParameters params) {
        if(!params.inHasTexture || params.finMaterial == null) {
            return super._on_fail_return;
        }
        final float[] pixels = params.finMaterial.getPixels();
        final int index = params.finMaterial.getIndex(params.finTexture[0], params.finTexture[1]);

        params.foutColor[0] = pixels[index];
        params.foutColor[1] = pixels[index + 1];
        params.foutColor[2] = pixels[index + 2];
        params.foutColor[3] = params.finMaterial.hasAlpha() ? pixels[index + 3] : 1.0f;

        return true;
    }
}
