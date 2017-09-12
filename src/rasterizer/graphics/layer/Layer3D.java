package rasterizer.graphics.layer;

import rasterizer.math.Matrix;
import rasterizer.model.Model;
import rasterizer.model.mesh.MeshData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ryan on 12/09/2017.
 */
public class Layer3D extends Layer {

    private float far, near;
    private final Matrix view, projection;

    private float[] zBuffer;

    private boolean pvDirty = true;
    private final Matrix projectionView = new Matrix(4, 4);

    private final List<Model> models = new ArrayList<>();

    public Layer3D(final int width, final int height) {
        super(width, height);
        this.view = new Matrix(4, 4, true);
        this.projection = new Matrix(4, 4);
        this.zBuffer = new float[super.getWidth() * super.getHeight()];
    }

    public void addModel(final Model model) {
        assert model != null;
        this.models.add(model);
    }

    public void removeModel(final Model model) {
        this.models.remove(model);
    }

    public void setToOrthographicProjection(final float far, final float near) {
        this.pvDirty = true;
        Matrix.setToOrthographicMatrix(this.projection, super.getWidth(), super.getHeight(), this.far = far, this.near = near);
    }

    public void setToPerspectiveProjection(final float fov, final float far, final float near) {
        this.pvDirty = true;
        Matrix.setToPerspectiveMatrix(this.projection, super.getWidth(), super.getHeight(), fov, this.far = far, this.near = near);
    }

    public Matrix getProjectionView() {
        if(this.pvDirty) {
            this.projection.multiply(this.view, this.projectionView);
            this.pvDirty = false;
        }
        return this.projectionView;
    }

    @Override
    public void render() {
        assert this.zBuffer != null : "Projection matrix probably not set";
        final Matrix temp4x1 = Matrix.TEMP_4x1.get(), temp4x4 = Matrix.TEMP_4x4.get();

        Arrays.fill(this.zBuffer, this.far);

        final Matrix projectionView = this.getProjectionView();
        for(final Model model : this.models) {
            final MeshData meshData = model.getMesh().getData();

            final int stride = meshData.getStride();
            final int[] indices = meshData.getIndices();
            final float[] data = meshData.getData(), buffer = meshData.getBuffer();
            final Matrix pvm = projectionView.multiply(model.getModelMatrix(), temp4x4);

            // Transform vertices by the pvm matrix
            for(final int idx : indices) {
                int index = idx * stride, offset = 0;

                // Point data
                temp4x1.fill(new float[]{data[index], data[index + 1], data[index + 2], 1.0f});
                pvm.multiply(temp4x1, temp4x1);

                buffer[index + offset++] = temp4x1.getElement(0);
                buffer[index + offset++] = temp4x1.getElement(1);
                buffer[index + offset++] = -temp4x1.getElement(2);

                // Normal data
                if(meshData.hasNormalData()) {
                    temp4x1.fill(new float[]{data[index + 3], data[index + 4], data[index + 5], 1.0f});
                    pvm.multiply(temp4x1, temp4x1);
                    buffer[index + offset++] = temp4x1.getElement(0);
                    buffer[index + offset++] = temp4x1.getElement(1);
                    buffer[index + offset++] = temp4x1.getElement(2);
                }

                // Texture data
                if(meshData.hasTextureData()) {
                    buffer[index + offset] = data[index + offset++];
                    buffer[index + offset] = data[index + offset];
                }
            }

            // Render faces
            for(int idx = 0; idx < indices.length; ) {
                // Get each index for the triangle
                final int idx1 = indices[idx++] * stride, idx2 = indices[idx++] * stride, idx3 = indices[idx++] * stride;

                // Get point location for each index
                final float px1 = buffer[idx1], py1 = buffer[idx1 + 1], pz1 = buffer[idx1 + 2];
                final float px2 = buffer[idx2], py2 = buffer[idx2 + 1], pz2 = buffer[idx2 + 2];
                final float px3 = buffer[idx3], py3 = buffer[idx3 + 1], pz3 = buffer[idx3 + 2];

                // Check near/far
                if((pz1 < this.near && pz2 < this.near && pz3 < this.near) || (pz1 > this.far && pz2 > this.far && pz3 > this.far)){
                    continue;
                }

                // Get triangle (rectangular) bounds. The bounds are also limited to fit within viewport (so no out-of-bounds pixels are iterated over)
                final int maxX = (int) Math.min(Math.max(px1, Math.max(px2, px3)), super.getWidth()),
                        minX = (int) Math.max(Math.min(px1, Math.min(px2, px3)), 0);
                final int maxY = (int) Math.min(Math.max(py1, Math.max(py2, py3)), super.getHeight()),
                        minY = (int) Math.max(Math.min(py1, Math.min(py2, py3)), 0);

                // Loop through each pixel in the bounds
                for(int j = minY; j < maxY; j++) {
                    for(int i = minX; i < maxX; i++) {
                        // Check to see if point is inside triangle using barycentric coordinates
                        final float pw1 = ((py2 - py3) * (i - px3) + (px3 - px2) * (j - py3))
                                / ((py2 - py3) * (px1 - px3) + (px3 - px2) * (py1 - py3));
                        if(pw1 < 0.0f || pw1 > 1.0f) {
                            continue;
                        }
                        final float pw2 = ((py3 - py1) * (i - px3) + (px1 - px3) * (j - py3))
                                / ((py2 - py3) * (px1 - px3) + (px3 - px2) * (py1 - py3));
                        if(pw2 < 0.0f || pw2 > 1.0f) {
                            continue;
                        }
                        final float pw3 = 1.0f - pw1 - pw2;
                        if(pw3 < 0.0f || pw3 > 1.0f) {
                            continue;
                        }

                        // If we get here, we know that the point is inside of the triangle and may need to be rendered

                        final float z = (pz1 * pw1 + pz2 * pw2 + pz3 * pw3);
                        // Check to see if this pixel is visible (according to near, far, and the depth buffer)
                        if(z < this.near || z > this.far){
                            continue;
                        }
                        final int depthIndex = i + j * super.getWidth();
                        if(z > this.zBuffer[depthIndex]){
                            //continue;
                        }

                        // If we get here, we are going to render the pixel. Lets update the depth buffer first
                        this.zBuffer[depthIndex] = z;
                        int rgb = (int)Math.min(255, Math.max(0, (this.far - this.near) / z));
                        super.setRGB(i, j, new Color(rgb, rgb, rgb, 255).getRGB());
                    }
                }
            }
        }
    }
}