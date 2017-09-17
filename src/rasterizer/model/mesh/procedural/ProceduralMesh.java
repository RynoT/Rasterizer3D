package rasterizer.model.mesh.procedural;

import rasterizer.model.mesh.Mesh;

/**
 * Created by Ryan on 17/09/2017.
 */
abstract class ProceduralMesh extends Mesh {

    int addVertex(int idx, final float[] data,
                            final float x, final float y, final float z,
                            final float nx, final float ny, final float nz,
                            final float u, final float v) {
        idx = this.addVertex(idx, data, x, y, z, nx, ny, nz);
        data[idx++] = u;
        data[idx++] = v;
        return idx;
    }

    int addVertex(int idx, final float[] data,
                  final float x, final float y, final float z,
                  final float nx, final float ny, final float nz) {
        data[idx++] = x;
        data[idx++] = y;
        data[idx++] = z;
        data[idx++] = nx;
        data[idx++] = ny;
        data[idx++] = nz;
        return idx;
    }

    int[] generateTriangleIndices(final int dataLength, final int vertexLength) {
        final int[] indices = new int[dataLength / vertexLength];
        for(int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        return indices;
    }

    int[] generateQuadIndices(final int dataLength, final int vertexLength) {
        final int[] template = {0, 1, 2, 2, 3, 0};
        final int[] indices = new int[6 * (dataLength / vertexLength) / 4];
        for(int i = 0; i < indices.length; i++) {
            indices[i] = template[i % template.length] + (i / template.length) * 4;
        }
        return indices;
    }
}
