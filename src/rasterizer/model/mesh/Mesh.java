package rasterizer.model.mesh;

import rasterizer.graphics.pass.FragmentPass;

/**
 * Created by Ryan on 12/09/2017.
 */
public class Mesh {

    private MeshData data = null;
    private MeshMaterial material = null;

    private FragmentPass fragmentPass = null;

    protected Mesh() {
    }

    public MeshData getData() {
        return this.data;
    }

    public MeshMaterial getMaterial() {
        return this.material;
    }

    public FragmentPass getFragmentPass() {
        return this.fragmentPass;
    }

    public void setMaterial(final MeshMaterial material) {
        this.material = material;
    }

    public void setFragmentPass(final FragmentPass fragmentPass) {
        this.fragmentPass = fragmentPass;
    }

    public void setData(final float[] data, final int[] indices, final int mask) {
        this.data = new MeshData(data, indices, mask);
    }
}
