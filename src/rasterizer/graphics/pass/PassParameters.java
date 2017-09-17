package rasterizer.graphics.pass;

import rasterizer.graphics.target.RenderTarget;
import rasterizer.math.Matrix;
import rasterizer.model.mesh.MeshData;
import rasterizer.model.mesh.MeshMaterial;

/**
 * Created by Ryan on 15/09/2017.
 */
public final class PassParameters {

    public boolean _skip_lighting = false;

    // General input parameters
    public int inWidth = 0;
    public int inHeight = 0;
    public boolean inHasNormal = false;
    public boolean inHasTexture = false;

    // Vertex pass input parameters
    public final float[] vinPoint = new float[MeshData.POINT_LENGTH];
    public final float[] vinNormal = new float[MeshData.NORMAL_LENGTH];
    public final float[] vinTexture = new float[MeshData.TEXTURE_LENGTH];

    public Matrix vinMatrixP = null, vinMatrixV = null, vinMatrixM = null;
    public Matrix vinMatrixPV = null;

    // Vertex pass output. These variables must be set by the vertex pass.
    public final float[] voutPoint = new float[MeshData.POINT_LENGTH];
    public final float[] voutNormal = new float[MeshData.NORMAL_LENGTH];
    public final float[] voutTexture = new float[MeshData.TEXTURE_LENGTH];

    public final Matrix voutMatrixVM = new Matrix(4, 4);
    public final Matrix voutMatrixPVM = new Matrix(4, 4);

    // Fragment pass input parameters
    public final float[] finPoint = new float[MeshData.POINT_LENGTH];
    public final float[] finNormal = new float[MeshData.NORMAL_LENGTH];
    public final float[] finTexture = new float[MeshData.TEXTURE_LENGTH];

    public final float[] finPointUnprojected = new float[MeshData.POINT_LENGTH]; // same as finPoint but without any matrix multiplications applied to it

    public MeshMaterial finMaterial = null;

    // Fragment pass output. This variable must be set by the fragment pass.
    public final float[] foutColor = new float[RenderTarget.RGBA_FLOAT_LENGTH]; //rgba

    public PassParameters copy() {
        final PassParameters instance = new PassParameters();

        instance._skip_lighting = this._skip_lighting;

        instance.inWidth = this.inWidth;
        instance.inHeight = this.inHeight;
        instance.inHasNormal = this.inHasNormal;
        instance.inHasTexture = this.inHasTexture;

        System.arraycopy(this.vinPoint, 0, instance.vinPoint, 0, this.vinPoint.length);
        System.arraycopy(this.vinNormal, 0, instance.vinNormal, 0, this.vinNormal.length);
        System.arraycopy(this.vinTexture, 0, instance.vinTexture, 0, this.vinTexture.length);

        instance.vinMatrixP = this.vinMatrixP;
        instance.vinMatrixV = this.vinMatrixV;
        instance.vinMatrixM = this.vinMatrixM;
        instance.vinMatrixPV = this.vinMatrixPV;

        System.arraycopy(this.voutPoint, 0, instance.voutPoint, 0, this.voutPoint.length);
        System.arraycopy(this.voutNormal, 0, instance.voutNormal, 0, this.voutNormal.length);
        System.arraycopy(this.voutTexture, 0, instance.voutTexture, 0, this.voutTexture.length);

        instance.voutMatrixVM.set(this.voutMatrixVM);
        instance.voutMatrixPVM.set(this.voutMatrixPVM);

        System.arraycopy(this.finPoint, 0, instance.finPoint, 0, this.finPoint.length);
        System.arraycopy(this.finNormal, 0, instance.finNormal, 0, this.finNormal.length);
        System.arraycopy(this.finTexture, 0, instance.finTexture, 0, this.finTexture.length);
        System.arraycopy(this.finPointUnprojected, 0, instance.finPointUnprojected, 0, this.finPointUnprojected.length);

        instance.finMaterial = this.finMaterial;

        System.arraycopy(this.foutColor, 0, instance.foutColor, 0, this.foutColor.length);
        return instance;
    }
}
