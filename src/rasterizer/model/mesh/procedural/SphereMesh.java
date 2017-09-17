package rasterizer.model.mesh.procedural;

import rasterizer.math.MathUtils;
import rasterizer.model.mesh.MeshData;

/**
 * Created by Ryan on 17/09/2017.
 */
class SphereMesh extends ProceduralMesh {

    SphereMesh(final float radius, final int horizontalSegments, final int verticalSegments) {
        final int vertexLength = MeshData.POINT_LENGTH + MeshData.NORMAL_LENGTH + MeshData.TEXTURE_LENGTH;
        final float theta = MathUtils.PI2 / verticalSegments, delta = MathUtils.PI / horizontalSegments;
        final float[] data = new float[vertexLength * 4 * horizontalSegments * verticalSegments];

        int di = 0;
        float x, y, z, u, v;
        for(float longitude = 0.0f; longitude < MathUtils.PI2; longitude += theta) {
            for(float latitude = 0.0f; latitude < MathUtils.PI; latitude += delta) {
                x = MathUtils.cos(longitude + theta) * MathUtils.sin(latitude + delta);
                y = MathUtils.cos(latitude + delta);
                z = MathUtils.sin(longitude + theta) * MathUtils.sin(latitude + delta);
                u = 1.0f - (longitude + theta) / MathUtils.PI2 - 0.25f;
                v = (latitude + delta) / MathUtils.PI;
                di = super.addVertex(di, data, x * radius, y * radius, z * radius, x, y, z, u, v);

                x = MathUtils.cos(longitude) * MathUtils.sin(latitude + delta);
                y = MathUtils.cos(latitude + delta);
                z = MathUtils.sin(longitude) * MathUtils.sin(latitude + delta);
                u = 1.0f - longitude / MathUtils.PI2 - 0.25f;
                v = (latitude + delta - MathUtils.PI / 180.0f) / MathUtils.PI;
                di = super.addVertex(di, data, x * radius, y * radius, z * radius, x, y, z, u, v);

                x = MathUtils.cos(longitude) * MathUtils.sin(latitude);
                y = MathUtils.cos(latitude);
                z = MathUtils.sin(longitude) * MathUtils.sin(latitude);
                u = 1.0f - longitude / MathUtils.PI2 - 0.25f;
                v = latitude / MathUtils.PI;
                di = super.addVertex(di, data, x * radius, y * radius, z * radius, x, y, z, u, v);

                x = MathUtils.cos(longitude + theta) * MathUtils.sin(latitude);
                y = MathUtils.cos(latitude);
                z = MathUtils.sin(longitude + theta) * MathUtils.sin(latitude);
                u = 1.0f - (longitude + theta) / MathUtils.PI2 - 0.25f;
                v = latitude / MathUtils.PI;
                di = super.addVertex(di, data, x * radius, y * radius, z * radius, x, y, z, u, v);
            }
        }
        super.setData(data, super.generateQuadIndices(data.length, vertexLength), MeshData.POINT_MASK | MeshData.NORMAL_MASK | MeshData.TEXTURE_MASK);
    }
}
