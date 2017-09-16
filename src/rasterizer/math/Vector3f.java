package rasterizer.math;

/**
 * Created by Ryan on 27/02/2017.
 */
public class Vector3f {

    public float x, y, z;

    public Vector3f() {
        this(0.0f);
    }

    public Vector3f(final float xyz) {
        this(xyz, xyz, xyz);
    }

    public Vector3f(final float[] xyz) {
        this(xyz[0], xyz[1], xyz[2]);
    }

    public Vector3f(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public void setX(final float x) {
        this.x = x;
    }

    public void setY(final float y) {
        this.y = y;
    }

    public void setZ(final float z) {
        this.z = z;
    }

    public Vector3f set(final Vector3f vector) {
        return this.set(vector.x, vector.y, vector.z);
    }

    public Vector3f set(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public boolean isOne() {
        return this.x == 1.0f && this.y == 1.0f && this.z == 1.0f;
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f && this.z == 0.0f;
    }

    public float getLength() {
        return MathUtils.sqrt(this.getLengthSq());
    }

    public float getLengthSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3f negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Vector3f translate(final float dx, final float dy, final float dz) {
        this.x += dx;
        this.y += dy;
        this.z += dz;
        return this;
    }

    public Vector3f normalize() {
        return this.normalize(this);
    }

    public Vector3f normalize(final Vector3f out) {
        final float len = 1.0f / this.getLength();
        out.x = this.x * len;
        out.y = this.y * len;
        out.z = this.z * len;
        return out;
    }

    public Matrix asMatrix() {
        return this.asMatrix(0.0f);
    }

    public Matrix asMatrix(final float w) {
        return new Matrix(4, 1).fill(new float[]{ this.x, this.y, this.z, w });
    }

    @Override
    public String toString() {
        return "[Vector3f] x: " + this.x + ", y: " + this.y + ", z: " + this.z;
    }

    ////

    public static Vector3f add(final Vector3f a, final Vector3f b) {
        return Vector3f.add(a, b, a);
    }

    public static Vector3f add(final Vector3f a, final Vector3f b, final Vector3f out) {
        out.x = a.x + b.x;
        out.y = a.y + b.y;
        out.z = a.z + b.z;
        return out;
    }

    public static Vector3f subtract(final Vector3f a, final Vector3f b) {
        return Vector3f.subtract(a, b, a);
    }

    public static Vector3f subtract(final Vector3f a, final Vector3f b, final Vector3f out) {
        out.x = a.x - b.x;
        out.y = a.y - b.y;
        out.z = a.z - b.z;
        return out;
    }

    public static Vector3f multiply(final Vector3f a, final Vector3f b) {
        return Vector3f.multiply(a, b, a);
    }

    public static Vector3f multiply(final Vector3f a, final Vector3f b, final Vector3f out) {
        out.x = a.x * b.x;
        out.y = a.y * b.y;
        out.z = a.z * b.z;
        return out;
    }

    public static Vector3f multiply(final Vector3f a, final Matrix b) {
        return Vector3f.multiply(a, b, a);
    }

    public static Vector3f multiply(final Vector3f a, final Matrix b, final Vector3f out) {
        final Matrix matrix = b.multiply(a.asMatrix(0.0f), new Matrix(4, 1));
        out.x = matrix.elements[0];
        out.y = matrix.elements[1];
        out.z = matrix.elements[2];
        return out;
    }

    public static Vector3f divide(final Vector3f a, final Vector3f b) {
        return Vector3f.divide(a, b, a);
    }

    public static Vector3f divide(final Vector3f a, final Vector3f b, final Vector3f out) {
        out.x = a.x / b.x;
        out.y = a.y / b.y;
        out.z = a.z / b.z;
        return out;
    }

    public static float dot(final Vector3f a, final Vector3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vector3f cross(final Vector3f a, final Vector3f b) {
        return Vector3f.cross(a, b, a);
    }

    public static Vector3f cross(final Vector3f a, final Vector3f b, final Vector3f out) {
        final float x = a.y * b.z - a.z * b.y, y = -(a.x * b.z - a.z * b.x), z = a.x * b.y - a.y * b.x;
        out.x = x;
        out.y = y;
        out.z = z;
        return out;
    }
}
