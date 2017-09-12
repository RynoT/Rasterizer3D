package rasterizer.model.mesh;

/**
 * Created by Ryan Thomson on 07/01/2017.
 * <p>
 * Data must be CCW (vertex, normal, texture) and indices must be triangles to render properly (+z is depth (where 0.0 is ui plane), +y is up screen)
 */
public class MeshData {

    public static final int POINT_LENGTH = 3, TEXTURE_LENGTH = 2, NORMAL_LENGTH = 3;
    public static final int POINT_MASK = 0x1, TEXTURE_MASK = 0x2, NORMAL_MASK = 0x4;

    private final int mask, stride;

    private final float[] data, buffer; //buffer is used by the renderer to store data changes without modifying the data
    private final int[] indices;

    private final int pointCount;

    // Data must be in triangle topology
    // Data must be in order (point, normal, texture) with normal and texture both being optional
    MeshData(final float[] data, final int[] indices, final int mask) {
        assert data != null && indices != null;
        this.mask = mask;
        this.data = data;
        this.indices = indices;
        this.buffer = new float[data.length];

        assert indices.length % 3 == 0 : "Invalid index data";
        assert this.hasPointData() : "All models must have point data";

        int stride = 0;
        if(this.hasPointData()) {
            stride += MeshData.POINT_LENGTH;
        }
        if(this.hasNormalData()) {
            stride += MeshData.NORMAL_LENGTH;
        }
        if(this.hasTextureData()) {
            stride += MeshData.TEXTURE_LENGTH;
        }
        this.stride = stride;

        int maxIndex = 0;
        for(final int index : indices) {
            assert index >= 0 : "Bad index";
            maxIndex = Math.max(index, maxIndex);
        }
        this.pointCount = maxIndex + 1;
    }

    public int getStride() {
        return this.stride;
    }

    public float[] getData() {
        return this.data;
    }

    public float[] getBuffer() {
        return this.buffer;
    }

    public int getPointCount() {
        return this.pointCount;
    }

    public int[] getIndices() {
        return this.indices;
    }

    public boolean hasPointData(){
        return (this.mask & MeshData.POINT_MASK) == MeshData.POINT_MASK;
    }

    public boolean hasNormalData(){
        return (this.mask & MeshData.NORMAL_MASK) == MeshData.NORMAL_MASK;
    }

    public boolean hasTextureData(){
        return (this.mask & MeshData.TEXTURE_MASK) == MeshData.TEXTURE_MASK;
    }

}
