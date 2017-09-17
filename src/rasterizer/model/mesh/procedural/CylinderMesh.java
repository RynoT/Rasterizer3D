package rasterizer.model.mesh.procedural;

import rasterizer.math.MathUtils;
import rasterizer.model.mesh.MeshData;

/**
 * Created by Ryan on 17/09/2017.
 */
class CylinderMesh extends ProceduralMesh {

    CylinderMesh(final float radius, final float height, final int horizontalSegments, final int verticalSegments) {
        final float horizontalInterval = height / horizontalSegments, verticalInterval = MathUtils.PI2 / verticalSegments;

        float theta = 0.0f;
        final int vertexLength = MeshData.POINT_LENGTH + MeshData.NORMAL_LENGTH;
        final float[] data = new float[vertexLength * 6 * vertexLength * 6 * horizontalSegments];

        for(int i = 0, di = 0; i < verticalSegments; i++, theta += verticalInterval) {
            final float xOuterA = MathUtils.cos(theta) * radius, xOuterB = MathUtils.cos(theta - verticalInterval) * radius;
            final float zOuterA = MathUtils.sin(theta) * radius, zOuterB = MathUtils.sin(theta - verticalInterval) * radius;

            //lower disk (bottom)
            di = super.addVertex(di, data, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f);
            di = super.addVertex(di, data, xOuterB, 0.0f, zOuterB, 0.0f, -1.0f, 0.0f);
            di = super.addVertex(di, data, xOuterA, 0.0f, zOuterA, 0.0f, -1.0f, 0.0f);

            for(int j = 0; j < horizontalSegments; j++) {
                final float delta = j * horizontalInterval, nextDelta = delta + horizontalInterval;

                //quad making up horizonal section of cylinder (two triangles)
                di = super.addVertex(di, data, xOuterA, delta, zOuterA, xOuterA, 0.0f, zOuterA);
                di = super.addVertex(di, data, xOuterB, delta, zOuterB, xOuterB, 0.0f, zOuterB);
                di = super.addVertex(di, data, xOuterB, nextDelta, zOuterB, xOuterB, 0.0f, zOuterB);

                di = super.addVertex(di, data, xOuterB, nextDelta, zOuterB, xOuterB, 0.0f, zOuterB);
                di = super.addVertex(di, data, xOuterA, nextDelta, zOuterA, xOuterA, 0.0f, zOuterA);
                di = super.addVertex(di, data, xOuterA, delta, zOuterA, xOuterA, 0.0f, zOuterA);
            }
            //upper disk (top)
            di = super.addVertex(di, data, 0.0f, height, 0.0f, 0.0f, 1.0f, 0.0f);
            di = super.addVertex(di, data, xOuterA, height, zOuterA, 0.0f, 1.0f, 0.0f);
            di = super.addVertex(di, data, xOuterB, height, zOuterB, 0.0f, 1.0f, 0.0f);
        }
        super.setData(data, super.generateTriangleIndices(data.length, vertexLength), MeshData.POINT_MASK | MeshData.NORMAL_MASK);
    }
}
