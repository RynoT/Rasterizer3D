package rasterizer.graphics.pass;

import rasterizer.graphics.target.RenderTarget;
import rasterizer.math.Matrix;
import rasterizer.model.mesh.MeshData;
import rasterizer.model.mesh.MeshMaterial;

/**
 * Created by Ryan on 15/09/2017.
 */
public class PassParameters {

    // FragmentParameters must be ThreadLocal so that we can multi-thread Layer3D rendering.
    private static final ThreadLocal<PassParameters> PARAMETERS = ThreadLocal.withInitial(PassParameters::new);

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

    private PassParameters(){
    }

    public static PassParameters get(){
        return PassParameters.PARAMETERS.get();
    }
}
