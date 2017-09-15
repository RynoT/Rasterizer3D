package rasterizer.model;

import rasterizer.math.Matrix;
import rasterizer.math.Vector3f;
import rasterizer.model.mesh.Mesh;

/**
 * Created by Ryan on 12/09/2017.
 */
public class Model {

    private Mesh mesh;

    private final Vector3f position = new Vector3f(0.0f);
    private final Vector3f rotation = new Vector3f(0.0f);
    private final Vector3f scale = new Vector3f(1.0f);

    private boolean modelDirty = true;
    private final Matrix modelMatrix = new Matrix(4, 4);

    public boolean _displayFlush = false;
    public boolean _flushPostRender = false;

    public Model(){
        this(null);
    }

    public Model(final Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public Vector3f getScale() {
        return this.scale;
    }

    // Modification of the returned value may have undesired effects
    public Vector3f getPosition() {
        return this.position;
    }

    // Modification of the returned value may have undesired effects
    public Vector3f getRotation() {
        return this.rotation;
    }

    public void setMesh(final Mesh mesh) {
        this.mesh = mesh;
    }

    public void setPosition(final float x, final float y) {
        this.setPosition(x, y, this.position.getZ());
    }

    public void setPosition(final float x, final float y, final float z) {
        this.modelDirty = true;
        this.position.set(x, y, z);
    }

    public void setRotation(final float z) {
        this.setRotation(this.rotation.getX(), this.rotation.getY(), z);
    }

    public void setRotation(final float x, final float y, final float z) {
        this.modelDirty = true;
        this.rotation.set(x, y, z);
    }

    public void setScale(final float xyz) {
        this.setScale(xyz, xyz, xyz);
    }

    public void setScale(final float x, final float y, final float z) {
        this.modelDirty = true;
        this.scale.set(x, y, z);
    }

    public void scale(final float xyz) {
        this.scale(xyz, xyz, xyz);
    }

    public void scale(final float x, final float y, final float z) {
        this.modelDirty = true;
        this.scale.translate(x, y, z);
    }

    public void rotate(final float dz) {
        this.rotate(0.0f, 0.0f, dz);
    }

    public void rotate(final float dx, final float dy, final float dz) {
        this.modelDirty = true;
        this.rotation.translate(dx, dy, dz);
    }

    public void translate(final float dx, final float dy) {
        this.translate(dx, dy, 0.0f);
    }

    public void translate(final float dx, final float dy, final float dz) {
        this.modelDirty = true;
        this.position.translate(dx, dy, dz);
    }

    public Matrix getModelMatrix() {
        if(this.modelDirty) {
            this.modelMatrix.setIdentity();

            final Matrix temp = Matrix.TEMP_4x4.get();
            if(!this.scale.isOne()) {
                this.modelMatrix.multiply(Matrix.setToScaleMatrix(temp, this.scale));
            }
            if(!this.position.isZero()) {
                this.modelMatrix.multiply(Matrix.setToTranslationMatrix(temp, this.position));
            }
            if(!this.rotation.isZero()) {
                this.modelMatrix.multiply(Matrix.setToRotationMatrix(temp, this.rotation));
            }
            this.modelDirty = false;
        }
        return this.modelMatrix;
    }
}
