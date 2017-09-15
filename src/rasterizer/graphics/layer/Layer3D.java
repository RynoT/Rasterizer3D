package rasterizer.graphics.layer;

import rasterizer.graphics.pass.FColorPass;
import rasterizer.graphics.pass.FragmentPass;
import rasterizer.math.Matrix;
import rasterizer.model.Model;
import rasterizer.model.mesh.Mesh;
import rasterizer.model.mesh.MeshData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ryan on 12/09/2017.
 */
public class Layer3D extends Layer {

    public boolean _render3d = true;
    public boolean _cullFaces = true; //front faces must be CCW if enabled
    public boolean _useZBuffer = true;
    public boolean _processNormalData = true;
    public boolean _processTextureData = true;
    public FragmentPass _defaultFragmentPass = new FColorPass(Color.WHITE);

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
        if(!this._render3d) {
            return;
        }
        super.clear();
        if(this._useZBuffer) {
            Arrays.fill(this.zBuffer, this.far);
        }

        final Matrix temp4x1 = Matrix.TEMP_4x1.get(), temp4x4 = Matrix.TEMP_4x4.get();

        final float[] rgbaBuffer = new float[4];
        final Matrix projectionView = this.getProjectionView();
        final FragmentPass.FragmentParameters fParams = FragmentPass.getParameters();
        for(final Model model : this.models) {
            final Mesh mesh = model.getMesh();
            if(mesh == null) {
                continue;
            }
            FragmentPass fPass = mesh.getFragmentPass();
            if(fPass == null) {
                if(this._defaultFragmentPass != null) {
                    fPass = this._defaultFragmentPass;
                } else {
                    continue; // no fragment pass means nothing can be rendered
                }
            }
            if(this._processTextureData) {
                fParams.material = mesh.getMaterial();
            }

            final MeshData meshData = mesh.getData();
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

                float z = -temp4x1.getElement(2);
                buffer[index + offset++] = super.getWidth() * (temp4x1.getElement(0) / z) / 2.0f + super.getWidth() / 2.0f;
                buffer[index + offset++] = super.getHeight() * (temp4x1.getElement(1) / z) / 2.0f + super.getHeight() / 2.0f;
                buffer[index + offset++] = 1.0f / z;

                // Normal data
                if(this._processNormalData && meshData.hasNormalData()) {
                    temp4x1.fill(new float[]{data[index + 3], data[index + 4], data[index + 5], 1.0f});
                    pvm.multiply(temp4x1, temp4x1);
                    buffer[index + offset++] = temp4x1.getElement(0);
                    buffer[index + offset++] = temp4x1.getElement(1);
                    buffer[index + offset++] = temp4x1.getElement(2);
                }

                // Texture data
                if(this._processTextureData && meshData.hasTextureData()) {
                    buffer[index + offset] = data[index + offset++] / z;
                    buffer[index + offset] = data[index + offset] / z;
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
                if((pz1 < this.near && pz2 < this.near && pz3 < this.near) || (pz1 > this.far && pz2 > this.far && pz3 > this.far)) {
                    continue;
                }

                // Check for back-facing. Front facing should be CCW (>= 0.0f, CW is < 0.0f) (Check Z component using cross product formula: (B - A) x (C - A))
                if(this._cullFaces && (px2 - px1) * (py3 - py1) - (py2 - py1) * (px3 - px1) >= 0.0f) {
                    continue;
                }

                // Get triangle (rectangular) bounds. The bounds are also limited to fit within viewport (so no out-of-bounds pixels are iterated over)
                final int maxX = Math.round(Math.min(Math.max(px1, Math.max(px2, px3)), super.getWidth() - 1)),
                        minX = Math.round(Math.max(Math.min(px1, Math.min(px2, px3)), 0.0f));
                final int maxY = Math.round(Math.min(Math.max(py1, Math.max(py2, py3)), super.getHeight() - 1)),
                        minY = Math.round(Math.max(Math.min(py1, Math.min(py2, py3)), 0.0f));

                super.prepareRGBA(maxX, maxY, minX, minY);

                // Loop through each pixel in the bounds
                for(int j = minY; j <= maxY; j++) {
                    for(int i = minX; i <= maxX; i++) {
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

                        final float z = 1.0f / (pz1 * pw1 + pz2 * pw2 + pz3 * pw3);
                        // Check to see if this pixel is visible (according to near, far, and the depth buffer)
                        if(z < this.near || z > this.far) {
                            continue;
                        }
                        if(this._useZBuffer) {
                            final int depthIndex = i + j * super.getWidth();
                            if(z > this.zBuffer[depthIndex]) {
                                continue;
                            }
                            // If we get here, we are going to render the pixel. Lets update the depth buffer first
                            this.zBuffer[depthIndex] = z;
                        }
                        // Setup fragment pass parameters
                        fParams.point[0] = i;
                        fParams.point[1] = j;
                        fParams.point[2] = z;

                        int offset = MeshData.POINT_LENGTH;
                        if(fParams.hasNormal = (this._processNormalData && meshData.hasNormalData())) {
                            fParams.normal[0] = (buffer[idx1 + offset] * pw1 + buffer[idx2 + offset] * pw2 + buffer[idx3 + offset] * pw3) * z;
                            fParams.normal[1] = (buffer[idx1 + 1 + offset] * pw1 + buffer[idx2 + 1 + offset] * pw2 + buffer[idx3 + 1 + offset] * pw3) * z;
                            fParams.normal[2] = (buffer[idx1 + 2 + offset] * pw1 + buffer[idx2 + 2 + offset] * pw2 + buffer[idx3 + 2 + offset] * pw3) * z;

                            offset += MeshData.NORMAL_LENGTH;
                        }
                        if(fParams.hasTexture = (this._processTextureData && meshData.hasTextureData())) {
                            fParams.texture[0] = (buffer[idx1 + offset] * pw1 + buffer[idx2 + offset] * pw2 + buffer[idx3 + offset] * pw3) * z;
                            fParams.texture[1] = (buffer[idx1 + 1 + offset] * pw1 + buffer[idx2 + 1 + offset] * pw2 + buffer[idx3 + 1 + offset] * pw3) * z;
                        }

                        if(!fPass.pass(rgbaBuffer) && (this._defaultFragmentPass == null || !this._defaultFragmentPass.pass(rgbaBuffer))) {
                            // If the fragment pass fails, we cannot render this pixel
                            continue;
                        }
                        super.setRGBA(rgbaBuffer, i, j);
                    }
                }
            }
            // To avoid setting as many unused pixels as possible, we will flush the rgba after each model has been rendered if it requests so.
            if(model._flushPostRender) {
                super.flushRGBA(model._displayFlush);
            }
        }
    }
}
