package rasterizer.model.mesh.basic;

import rasterizer.model.mesh.Mesh;
import rasterizer.model.mesh.MeshData;

/**
 * Created by Ryan on 12/09/2017.
 */
public class QuadMesh extends Mesh {

    private final float xLen, yLen;

    // Default centered
    public QuadMesh(final float xLen, final float yLen) {
        this(xLen, yLen, true);
    }

    // If not centered, origin is at top left
    public QuadMesh(final float xLen, final float yLen, final boolean centered) {
        this.xLen = xLen;
        this.yLen = yLen;

        final float[] buffer;
        if(centered) {
            final float hx = xLen / 2.0f, hy = yLen / 2.0f;
            buffer = new float[]{
                    -hx, -hy, 0.0f, 0.0f, 0.0f, //top left
                    -hx, hy, 0.0f, 0.0f, 1.0f, //bottom left
                    hx, hy, 0.0f, 1.0f, 1.0f, //bottom right
                    hx, -hy, 0.0f, 1.0f, 0.0f, //top right
            };
        } else {
            buffer = new float[]{
                    0.0f, 0.0f, 0.0f, 0.0f, 0.0f, //top left
                    0.0f, yLen, 0.0f, 0.0f, 1.0f, //bottom left
                    xLen, yLen, 0.0f, 1.0f, 1.0f, //bottom right
                    xLen, 0.0f, 0.0f, 1.0f, 0.0f, //top right
            };
        }
        final int[] indices = { 0, 1, 2, 2, 3, 0 };
        super.setData(buffer, indices, MeshData.POINT_MASK | MeshData.TEXTURE_MASK);
    }

    public float getXLen() {
        return this.xLen;
    }

    public float getYLen() {
        return this.yLen;
    }
}