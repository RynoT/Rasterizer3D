package rasterizer.graphics.pass;

import rasterizer.model.mesh.MeshMaterial;

import java.util.Arrays;

/**
 * Created by Ryan on 14/09/2017.
 */
public abstract class FragmentPass {

    // FragmentParameters must be ThreadLocal so that we can multi-thread Layer3D rendering.
    private static final ThreadLocal<FragmentParameters> PARAMETERS = ThreadLocal.withInitial(FragmentParameters::new);

    public abstract void pass(final float[] outRGBA);

    public static FragmentParameters getParameters(){
        return FragmentPass.PARAMETERS.get();
    }

    public static final class FragmentParameters {

        public MeshMaterial material;

        public boolean hasNormal = false, hasTexture = false;
        public final float[] point = new float[3], normal = new float[3], texture = new float[2];

        private FragmentParameters(){
            this.clean(); // Not really necessary but should be here in case we change the clean method
        }

        // Reset all local variables to their initial values
        public void clean(){
            this.material = null;
            this.hasNormal = false;
            this.hasTexture = false;
            Arrays.fill(this.point, 0.0f);
            Arrays.fill(this.normal, 0.0f);
            Arrays.fill(this.texture, 0.0f);
        }
    }
}
