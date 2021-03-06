package rasterizer.graphics.pass;

import rasterizer.math.Matrix;

/**
 * Created by Ryan on 15/09/2017.
 */
public class VProjectionPass implements VertexPass {

    @Override
    public void pass(final PassParameters params) {
        final Matrix m4x1 = new Matrix(4, 1);

        params.vinMatrixV.multiply(params.vinMatrixM, params.voutMatrixVM);
        params.vinMatrixPV.multiply(params.vinMatrixM, params.voutMatrixPVM);

        // Calculate point data
        final float[] raw4x1 = m4x1.elements;
        raw4x1[0] = params.vinPoint[0];
        raw4x1[1] = params.vinPoint[1];
        raw4x1[2] = params.vinPoint[2];
        raw4x1[3] = 1.0f;

        params.voutMatrixPVM.multiply(m4x1, m4x1);

        final float z = -raw4x1[2];
        params.voutPoint[0] = params.inWidth * (raw4x1[0] / z) / 2.0f + params.inWidth / 2.0f;
        params.voutPoint[1] = params.inHeight * (raw4x1[1] / z) / 2.0f + params.inHeight / 2.0f;
        params.voutPoint[2] = 1.0f / z;

        // Calculate normal data
        if(params.inHasNormal) {
            raw4x1[0] = params.vinNormal[0];
            raw4x1[1] = params.vinNormal[1];
            raw4x1[2] = params.vinNormal[2];
            raw4x1[3] = 0.0f;

            params.voutMatrixVM.multiply(m4x1, m4x1);
            params.voutNormal[0] = raw4x1[0];
            params.voutNormal[1] = raw4x1[1];
            params.voutNormal[2] = raw4x1[2];
        }

        // Calculate texture data
        if(params.inHasTexture) {
            params.voutTexture[0] = params.vinTexture[0] / z;
            params.voutTexture[1] = params.vinTexture[1] / z;
        }
    }
}
