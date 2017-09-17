package rasterizer.model.mesh.procedural;

import rasterizer.model.mesh.Mesh;

/**
 * Created by Ryan on 17/09/2017.
 */
public final class ProceduralMeshFactory {

    private ProceduralMeshFactory() {
    }

    public static Mesh createCube(final float xLen, final float yLen, final float zLen) {
        return new CubeMesh(xLen, yLen, zLen);
    }

    public static Mesh createCone(final float radius, final float height, final int segments) {
        return new ConeMesh(radius, height, segments);
    }

    public static Mesh createCylinder(final float radius, final float height, final int horizontalSegments, final int verticalSegments) {
        return new CylinderMesh(radius, height, horizontalSegments, verticalSegments);
    }

    public static Mesh createQuad(final float xLen, final float yLen) {
        return ProceduralMeshFactory.createQuad(xLen, yLen, true);
    }

    public static Mesh createQuad(final float xLen, final float yLen, final boolean centered) {
        return new QuadMesh(xLen, yLen, centered);
    }

    public static Mesh createSphere(final float radius, final int horizontalSegments, final int verticalSegments) {
        return new SphereMesh(radius, horizontalSegments, verticalSegments);
    }
}
