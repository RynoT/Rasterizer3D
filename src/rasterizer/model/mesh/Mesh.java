package rasterizer.model.mesh;

/**
 * Created by Ryan on 12/09/2017.
 */
public class Mesh {

    private MeshData data = null;
    private MeshMaterial material = null;

    protected Mesh() {
    }

    public MeshData getData() {
        return this.data;
    }

    public MeshMaterial getMaterial() {
        return this.material;
    }

    public void setMaterial(final MeshMaterial material) {
        this.material = material;
    }

    public void setData(final float[] data, final int[] indices, final int mask) {
        this.data = new MeshData(data, indices, mask);
    }
}
