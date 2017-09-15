package rasterizer.graphics.layer;

import rasterizer.graphics.pass.*;
import rasterizer.math.MathUtils;
import rasterizer.math.Matrix;
import rasterizer.model.Model;
import rasterizer.model.mesh.Mesh;
import rasterizer.model.mesh.MeshData;
import rasterizer.model.mesh.MeshMaterial;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * Created by Ryan on 12/09/2017.
 */
public class Layer3D extends Layer {

    public boolean _render3d = true;
    public boolean _cull_faces = true; //front faces must be CCW if enabled
    public boolean _use_z_buffer = true;
    public boolean _process_normal_data = true;
    public boolean _process_texture_data = true;
    public boolean _render_async_threadsafe = true;
    public MeshMaterial _default_material = null;
    public VertexPass _default_vertex_pass = new VProjectionPass();
    public FragmentPass _default_fragment_pass = new FColorPass(Color.WHITE);

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

    public List<RecursiveAction> renderAsync() {
        if(!this._render3d) {
            return null;
        }
        if(!super._render_async) {
            this.render();
            return null;
        }
        assert super._render_chunk_size > 0 && super._render_chunk_size <= 1.0f
                : "Chunk size must be a percentage between 0.0f (exclusive) and 1.0f (inclusive)";

        // We do not parallelise the vertex calculations. Only fragmentation.
        this.computeVertices();

        final int chunkSizeW = (int) (super.getWidth() * super._render_chunk_size);
        final int chunkSizeH = (int) (super.getHeight() * super._render_chunk_size);

        // Split the given dimensions into chunks that will be ran in parallel
        final List<RecursiveAction> actions = new ArrayList<>();
        final int wCount = super.getWidth() / chunkSizeW, hCount = super.getHeight() / chunkSizeH;
        for(int j = 0; j < hCount; j++) {
            final int newY = chunkSizeH * j;
            for(int i = 0; i < wCount; i++) {
                final int newX = chunkSizeW * i;
                final RecursiveAction action = new RecursiveAction() {
                    @Override
                    protected void compute() {
                        Layer3D.this.computeFragments(newX, newY, chunkSizeW, chunkSizeH);
                    }
                };
                action.fork();
                actions.add(action);
            }
        }
        final int remainingW = super.getWidth() - wCount * chunkSizeW,
                remainingH = super.getHeight() - hCount * chunkSizeH;
        if(remainingW > 0 && remainingH > 0) {
            // Render the remaining left and bottom. We do both on this thread, that's why it's better that the chunk size leaves 0 remaining.
            this.computeFragments(super.getWidth() - remainingW, 0, remainingW, super.getHeight());
            this.computeFragments(0, super.getHeight() - remainingH, super.getWidth() - remainingW, remainingH);
        }
        return actions;
    }

    @Override
    public void render() {
        if(!this._render3d) {
            return;
        }
        this.computeVertices();
        this.computeFragments(0, 0, super.getWidth(), super.getHeight());
    }

    private void computeVertices() {
        // Clear the buffer
        super.clear();

        // Reset the depth buffer
        if(this._use_z_buffer) {
            Arrays.fill(this.zBuffer, this.far);
        }

        // We want to render async but there are prerequisites that we need to do before fragmentation can happen.
        // We do not parallelise the vertex processing stage. That is what we are doing here.
        for(final Model model : this.models) {
            final Mesh mesh = model.getMesh();
            if(mesh == null || !mesh._render) {
                continue;
            }
            VertexPass vPass = mesh.getVertexPass();
            if(vPass == null) {
                if(this._default_vertex_pass != null) {
                    vPass = this._default_vertex_pass;
                } else {
                    continue; // no vertex pass means nothing can be rendered
                }
            }

            final MeshData meshData = mesh.getData();
            final PassParameters params = mesh.getPassParameters();

            params.inWidth = super.getWidth();
            params.inHeight = super.getHeight();
            params.inHasNormal = this._process_normal_data && mesh._process_normal_data && meshData.hasNormalData();
            params.inHasTexture = this._process_texture_data && mesh._process_texture_data && meshData.hasTextureData();
            params.vinProjectionMatrix = this.projection;
            params.vinViewMatrix = this.view;
            params.vinModelMatrix = model.getModelMatrix();
            params.vinProjectionViewMatrix = this.getProjectionView();
            if(this._process_texture_data) {
                params.finMaterial = mesh.getMaterial() == null ? this._default_material : mesh.getMaterial();
            }

            final int stride = meshData.getStride();
            final float[] data = meshData.getData(), buffer = meshData.getBuffer();
            final Rectangle.Float bounds = mesh.getScreenBounds();
            bounds.x = -1.0f;

            // Transform vertices by the pvm matrix
            for(final int idx : meshData.getIndices()) {
                int index = idx * stride, offset = 0;

                // Point data
                params.vinPoint[0] = data[index + offset++];
                params.vinPoint[1] = data[index + offset++];
                params.vinPoint[2] = data[index + offset++];

                if(params.inHasNormal) { // Normal data
                    params.vinNormal[0] = data[index + offset++];
                    params.vinNormal[1] = data[index + offset++];
                    params.vinNormal[2] = data[index + offset++];
                }
                if(params.inHasTexture) { // Texture data
                    params.vinTexture[0] = data[index + offset++];
                    params.vinTexture[1] = data[index + offset];
                }

                vPass.pass(params);

                // Set buffer data from vertex pass output
                offset = 0;
                buffer[index + offset++] = params.voutPoint[0];
                buffer[index + offset++] = params.voutPoint[1];
                buffer[index + offset++] = params.voutPoint[2];
                if(bounds.x == -1) {
                    bounds.setRect(params.voutPoint[0], params.voutPoint[1], 1.0f, 1.0f);
                } else if(!bounds.contains(params.voutPoint[0], params.voutPoint[1])) {
                    bounds.add(params.voutPoint[0], params.voutPoint[1]);
                }

                if(params.inHasNormal) {
                    buffer[index + offset++] = params.voutNormal[0];
                    buffer[index + offset++] = params.voutNormal[1];
                    buffer[index + offset++] = params.voutNormal[2];
                }
                if(params.inHasTexture) {
                    buffer[index + offset++] = params.voutTexture[0];
                    buffer[index + offset] = params.voutTexture[1];
                }
            }
            super.prepareRGBA((int) Math.ceil(bounds.x + bounds.width), (int) Math.ceil(bounds.y + bounds.height), (int) bounds.x, (int) bounds.y);
        }
    }

    private void computeFragments(final int x, final int y, int w, int h) {
        // Begin the fragmentation process
        for(final Model model : this.models) {

            final Mesh mesh = model.getMesh();
            if(mesh == null || !mesh._render) {
                continue;
            }

            // Check to see if the models screen bounds is within the render boundaries.
            // There's no point processing it if we're guaranteed not to render anything.
            final Rectangle.Float bounds = mesh.getScreenBounds();
            if(!bounds.intersects(x, y, w, h)) {
                continue;
            }

            // Find the fragment pass that we're going to use
            FragmentPass fPass = mesh.getFragmentPass();
            if(fPass == null) {
                if(this._default_fragment_pass != null) {
                    fPass = this._default_fragment_pass;
                } else {
                    continue; // no fragment pass means nothing can be rendered
                }
            }

            final MeshData meshData = mesh.getData();
            final int stride = meshData.getStride();
            final int[] indices = meshData.getIndices();
            PassParameters params = mesh.getPassParameters();
            if(super._render_async && this._render_async_threadsafe) {
                params = params.copy();
            }

            final float[] buffer = meshData.getBuffer();

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
                if(this._cull_faces && (px2 - px1) * (py3 - py1) - (py2 - py1) * (px3 - px1) >= 0.0f) {
                    continue;
                }

                // Get triangle (rectangular) bounds. The bounds are also limited to fit within viewport (so no out-of-bounds pixels are iterated over)
                final int maxX = MathUtils.round(MathUtils.min(MathUtils.max(px1, px2, px3), params.inWidth - 1));
                final int minX = MathUtils.round(MathUtils.max(MathUtils.min(px1, px2, px3), 0.0f));
                final int maxY = MathUtils.round(MathUtils.min(MathUtils.max(py1, py2, py3), params.inHeight - 1));
                final int minY = MathUtils.round(MathUtils.max(MathUtils.min(py1, py2, py3), 0.0f));

                // Loop through each pixel in the bounds
                for(int j = MathUtils.max(minY, y); j <= maxY; j++) {
                    if(j > y + h) {
                        break;
                    }
                    for(int i = MathUtils.max(minX, x); i <= maxX; i++) {
                        if(i > x + w) {
                            break;
                        }
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
                        if(this._use_z_buffer) {
                            final int depthIndex = i + j * super.getWidth();
                            if(z > this.zBuffer[depthIndex]) {
                                continue;
                            }
                            // If we get here, we are going to render the pixel. Lets update the depth buffer first
                            this.zBuffer[depthIndex] = z;
                        }
                        // Setup fragment pass parameters
                        params.finPoint[0] = i;
                        params.finPoint[1] = j;
                        params.finPoint[2] = z;

                        int offset = MeshData.POINT_LENGTH;
                        if(params.inHasNormal) {
                            params.finNormal[0] = (buffer[idx1 + offset] * pw1 + buffer[idx2 + offset] * pw2 + buffer[idx3 + offset] * pw3) * z;
                            params.finNormal[1] = (buffer[idx1 + 1 + offset] * pw1 + buffer[idx2 + 1 + offset] * pw2 + buffer[idx3 + 1 + offset] * pw3) * z;
                            params.finNormal[2] = (buffer[idx1 + 2 + offset] * pw1 + buffer[idx2 + 2 + offset] * pw2 + buffer[idx3 + 2 + offset] * pw3) * z;

                            offset += MeshData.NORMAL_LENGTH;
                        }
                        if(params.inHasTexture) {
                            params.finTexture[0] = (buffer[idx1 + offset] * pw1 + buffer[idx2 + offset] * pw2 + buffer[idx3 + offset] * pw3) * z;
                            params.finTexture[1] = (buffer[idx1 + 1 + offset] * pw1 + buffer[idx2 + 1 + offset] * pw2 + buffer[idx3 + 1 + offset] * pw3) * z;

                            //offset += MeshData.TEXTURE_LENGTH;
                        }

                        if(!fPass.pass(params) && (this._default_fragment_pass == null || !this._default_fragment_pass.pass(params))) {
                            // If the fragment pass fails, we cannot render this pixel
                            continue;
                        }
                        super.setRGBA(params.foutColor, i, j);
                    }
                }
            }
        }
    }
}
