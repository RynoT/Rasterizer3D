package rasterizer.graphics.pass;

import rasterizer.graphics.target.RenderTarget;
import rasterizer.math.Matrix;
import rasterizer.model.mesh.MeshData;
import rasterizer.model.mesh.MeshMaterial;

/**
 * Created by Ryan on 15/09/2017.
 */
public class PassParameters {

    // General input parameters
    public int inWidth = 0;
    public int inHeight = 0;
    public boolean inHasNormal = false;
    public boolean inHasTexture = false;

    // Vertex pass input parameters
    public final float[] vinPoint = new float[MeshData.POINT_LENGTH];
    public final float[] vinNormal = new float[MeshData.NORMAL_LENGTH];
    public final float[] vinTexture = new float[MeshData.TEXTURE_LENGTH];

    public Matrix vinProjectionMatrix = null, vinViewMatrix = null, vinModelMatrix = null;
    public Matrix vinProjectionViewMatrix = null;

    // Vertex pass output. These variables must be set by the vertex pass.
    public final float[] voutPoint = new float[MeshData.POINT_LENGTH];
    public final float[] voutNormal = new float[MeshData.NORMAL_LENGTH];
    public final float[] voutTexture = new float[MeshData.TEXTURE_LENGTH];

    // Fragment pass input parameters
    public final float[] finPoint = new float[MeshData.POINT_LENGTH];
    public final float[] finNormal = new float[MeshData.NORMAL_LENGTH];
    public final float[] finTexture = new float[MeshData.TEXTURE_LENGTH];

    public MeshMaterial finMaterial = null;

    // Fragment pass output. This variable must be set by the fragment pass.
    public final float[] foutColor = new float[RenderTarget.RGBA_FLOAT_LENGTH]; //rgba

    public PassParameters copy() {
        final PassParameters instance = new PassParameters();
        instance.inWidth = this.inWidth;
        instance.inHeight = this.inHeight;
        instance.inHasNormal = this.inHasNormal;
        instance.inHasTexture = this.inHasTexture;

        System.arraycopy(this.vinPoint, 0, instance.vinPoint, 0, this.vinPoint.length);
        System.arraycopy(this.vinNormal, 0, instance.vinNormal, 0, this.vinNormal.length);
        System.arraycopy(this.vinTexture, 0, instance.vinTexture, 0, this.vinTexture.length);

        instance.vinProjectionMatrix = this.vinProjectionMatrix;
        instance.vinViewMatrix = this.vinViewMatrix;
        instance.vinModelMatrix = this.vinModelMatrix;
        instance.vinProjectionViewMatrix = this.vinProjectionViewMatrix;

        System.arraycopy(this.voutPoint, 0, instance.voutPoint, 0, this.voutPoint.length);
        System.arraycopy(this.voutNormal, 0, instance.voutNormal, 0, this.voutNormal.length);
        System.arraycopy(this.voutTexture, 0, instance.voutTexture, 0, this.voutTexture.length);

        System.arraycopy(this.finPoint, 0, instance.finPoint, 0, this.finPoint.length);
        System.arraycopy(this.finNormal, 0, instance.finNormal, 0, this.finNormal.length);
        System.arraycopy(this.finTexture, 0, instance.finTexture, 0, this.finTexture.length);

        instance.finMaterial = this.finMaterial;

        System.arraycopy(this.foutColor, 0, instance.foutColor, 0, this.foutColor.length);
        return instance;
    }
}
