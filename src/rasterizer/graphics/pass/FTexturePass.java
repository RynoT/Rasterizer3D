package rasterizer.graphics.pass;

/**
 * Created by Ryan on 14/09/2017.
 */
public class FTexturePass extends FragmentPass {

    @Override
    public boolean pass(final float[] outRGBA) {
        final FragmentParameters parameters = FragmentPass.getParameters();

        if(!parameters.hasTexture) {
            return false;
        }
        assert parameters.material != null;
        final float[] pixels = parameters.material.getPixels();
        final int index = parameters.material.getIndex(parameters.texture[0], parameters.texture[1]);

        outRGBA[0] = pixels[index];
        outRGBA[1] = pixels[index + 1];
        outRGBA[2] = pixels[index + 2];
        outRGBA[3] = parameters.material.hasAlpha() ? pixels[index + 3] : 1.0f;
        return true;
    }
}
