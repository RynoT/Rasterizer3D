package rasterizer.model.mesh.procedural;

import rasterizer.math.MathUtils;
import rasterizer.model.mesh.MeshData;

/**
 * Created by Ryan on 17/09/2017.
 */
class ConeMesh extends ProceduralMesh {

    ConeMesh(final float radius, final float height, final int segments) {
        assert segments >= 3;

        final int vertexLength = MeshData.POINT_LENGTH + MeshData.NORMAL_LENGTH;
        final float interval = MathUtils.PI2 / (float) segments;
        final float[] data = new float[vertexLength * 6 * segments];

        float theta = MathUtils.PI / 4.0f;
        for(int i = 0, di = 0; i < segments; i++, theta += interval) {
            final float xOuterA = MathUtils.cos(theta) * radius, xOuterB = MathUtils.cos(theta + interval) * radius;
            final float zOuterA = MathUtils.sin(theta) * radius, zOuterB = MathUtils.sin(theta + interval) * radius;

            di = super.addVertex(di, data, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f);
            di = super.addVertex(di, data, xOuterA, 0.0f, zOuterA, 0.0f, -1.0f, 0.0f);
            di = super.addVertex(di, data, xOuterB, 0.0f, zOuterB, 0.0f, -1.0f, 0.0f);

            di = super.addVertex(di, data, xOuterB, 0.0f, zOuterB, xOuterB, 0.0f, zOuterB);
            di = super.addVertex(di, data, xOuterA, 0.0f, zOuterA, xOuterA, 0.0f, zOuterA);
            di = super.addVertex(di, data, 0.0f, height, 0.0f, xOuterA + (xOuterB - xOuterA) / 2.0f, 0.0f, zOuterA + (zOuterB - zOuterA) / 2.0f);
        }
        super.setData(data, super.generateTriangleIndices(data.length, vertexLength), MeshData.POINT_MASK | MeshData.NORMAL_MASK);
    }
}
