package rasterizer.math;

/**
 * Created by Ryan on 27/02/2017.
 */
public class Vector3f extends Vector2f {

    float z;

    public Vector3f() {
        this(0.0f);
    }

    public Vector3f(final float xyz) {
        this(xyz, xyz, xyz);
    }

    public Vector3f(final float x, final float y, final float z) {
        super(x, y);
        this.z = z;
    }

    public float getZ() {
        return this.z;
    }

    public void setZ(final float z) {
        this.z = z;
    }

    public Vector3f set(final float x, final float y, final float z) {
        super.x = x;
        super.y = y;
        this.z = z;
        return this;
    }

    @Override
    public boolean isOne() {
        return super.x == 1.0f && super.y == 1.0f && this.z == 1.0f;
    }

    @Override
    public boolean isZero() {
        return super.x == 0.0f && super.y == 0.0f && this.z == 0.0f;
    }

    @Override
    public Vector3f negate() {
        super.x = -super.x;
        super.y = -super.y;
        this.z = -this.z;
        return this;
    }

    public Vector3f translate(final float dx, final float dy, final float dz) {
        super.x += dx;
        super.y += dy;
        this.z += dz;
        return this;
    }

    public Vector3f cross(final Vector3f target){
        return this.cross(target, this);
    }

    public Vector3f cross(final Vector3f target, final Vector3f out){
        return out.set(super.y * target.z - this.z * target.y,
                -(super.x * target.z - this.z * target.x),
                super.x * target.y - super.y * target.x);
    }

    @Override
    public Matrix asMatrix() {
        return new Matrix(4, 1).fill(new float[]{ super.x, super.y, this.z, 0.0f });
    }
}
