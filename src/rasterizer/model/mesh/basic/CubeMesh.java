package rasterizer.model.mesh.basic;

import rasterizer.model.mesh.Mesh;
import rasterizer.model.mesh.MeshData;

/**
 * Created by Ryan Thomson on 25/05/2017.
 */
public class CubeMesh extends Mesh {

    public static final int POINTS_PER_FACE = 4;
    public static final int FACE_COUNT = 6;

    public CubeMesh(final float xLen, final float yLen, final float zLen) {
        final float hx = xLen / 2.0f, hy = yLen / 2.0f, hz = zLen / 2.0f;
        final float[] buffer = {
                -hx, hy, -hz, 0.0f, 1.0f, /**/ hx, hy, -hz, 1.0f, 1.0f, /**/ hx, -hy, -hz, 1.0f, 0.0f, /**/ -hx, -hy, -hz, 0.0f, 0.0f, //front
                -hx, hy, hz, 0.0f, 1.0f, /**/ -hx, -hy, hz, 1.0f, 1.0f, /**/ hx, -hy, hz, 1.0f, 0.0f, /**/ hx, hy, hz, 0.0f, 0.0f, //back
                hx, hy, -hz, 0.0f, 1.0f, /**/ hx, hy, hz, 1.0f, 1.0f, /**/ hx, -hy, hz, 1.0f, 0.0f, /**/ hx, -hy, -hz, 0.0f, 0.0f, //right
                -hx, hy, -hz, 0.0f, 1.0f, /**/ -hx, -hy, -hz, 1.0f, 1.0f, /**/ -hx, -hy, hz, 1.0f, 0.0f, /**/ -hx, hy, hz, 0.0f, 0.0f, //left
                -hx, hy, -hz, 0.0f, 1.0f, /**/ -hx, hy, hz, 1.0f, 1.0f, /**/ hx, hy, hz, 1.0f, 0.0f, /**/ hx, hy, -hz, 0.0f, 0.0f, //top
                -hx, -hy, -hz, 0.0f, 1.0f, /**/ hx, -hy, -hz, 1.0f, 1.0f, /**/ hx, -hy, hz, 1.0f, 0.0f, /**/ -hx, -hy, hz, 0.0f, 0.0f //bottom
        };
        final int[] indices = {
                0, 1, 2, 2, 3, 0,
                4, 5, 6, 6, 7, 4,
                8, 9, 10, 10, 11, 8,
                12, 13, 14, 14, 15, 12,
                16, 17, 18, 18, 19, 16,
                20, 21, 22, 22, 23, 20
        };
        super.setData(buffer, indices, MeshData.POINT_MASK | MeshData.TEXTURE_MASK);
    }
}
