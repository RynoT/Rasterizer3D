package rasterizer.model.mesh;

import rasterizer.graphics.pass.FragmentPass;
import rasterizer.graphics.pass.PassParameters;
import rasterizer.graphics.pass.VertexPass;

import java.awt.*;

/**
 * Created by Ryan on 12/09/2017.
 */
public class Mesh {

    public boolean _render = true;
    public boolean _process_normal_data = true;
    public boolean _process_texture_data = true;

    private MeshData data = null;
    private MeshMaterial material = null;

    private VertexPass vertexPass = null;
    private FragmentPass fragmentPass = null;
    private final PassParameters passParams = new PassParameters();

    private final Rectangle.Float screenBounds = new Rectangle.Float(); // set during renders

    protected Mesh() {
    }

    public MeshData getData() {
        return this.data;
    }

    public MeshMaterial getMaterial() {
        return this.material;
    }

    public Rectangle.Float getScreenBounds() {
        return this.screenBounds;
    }

    public VertexPass getVertexPass() {
        return this.vertexPass;
    }

    public FragmentPass getFragmentPass() {
        return this.fragmentPass;
    }

    public PassParameters getPassParameters() {
        return this.passParams;
    }

    public void setMaterial(final MeshMaterial material) {
        this.material = material;
    }

    public void setVertexPass(final VertexPass vertexPass) {
        this.vertexPass = vertexPass;
    }

    public void setFragmentPass(final FragmentPass fragmentPass) {
        this.fragmentPass = fragmentPass;
    }

    public void setData(final float[] data, final int[] indices, final int mask) {
        this.data = new MeshData(data, indices, mask);
    }
}
