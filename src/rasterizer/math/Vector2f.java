package rasterizer.math;

/**
 * Created by Ryan on 27/02/2017.
 */
public class Vector2f {

    public float x, y;

    public Vector2f() {
        this(0.0f);
    }

    public Vector2f(final float xy) {
        this(xy, xy);
    }

    public Vector2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(final float x) {
        this.x = x;
    }

    public void setY(final float y) {
        this.y = y;
    }

    public Vector2f set(final Vector2f vector) {
        return this.set(vector.x, vector.y);
    }

    public Vector2f set(final float x, final float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public boolean isOne() {
        return this.x == 1.0f && this.y == 1.0f;
    }

    public boolean isZero() {
        return this.x == 0.0f && this.y == 0.0f;
    }

    public float getLength() {
        return MathUtils.sqrt(this.getLengthSq());
    }

    public float getLengthSq() {
        return this.x * this.x + this.y * this.y;
    }

    public Vector2f negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    public Vector2f translate(final float dx, final float dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Matrix asMatrix() {
        return new Matrix(2, 1).fill(new float[]{this.x, this.y});
    }

    @Override
    public String toString() {
        return "[Vector2f] x: " + this.x + ", y: " + this.y;
    }
    
    ////

    public static Vector2f add(final Vector2f a, final Vector2f b) {
        return Vector2f.add(a, b, a);
    }

    public static Vector2f add(final Vector2f a, final Vector2f b, final Vector2f out) {
        out.x = a.x + b.x;
        out.y = a.y + b.y;
        return out;
    }

    public static Vector2f subtract(final Vector2f a, final Vector2f b) {
        return Vector2f.subtract(a, b, a);
    }

    public static Vector2f subtract(final Vector2f a, final Vector2f b, final Vector2f out) {
        out.x = a.x - b.x;
        out.y = a.y - b.y;
        return out;
    }

    public static Vector2f multiply(final Vector2f a, final Vector2f b) {
        return Vector2f.multiply(a, b, a);
    }

    public static Vector2f multiply(final Vector2f a, final Vector2f b, final Vector2f out) {
        out.x = a.x * b.x;
        out.y = a.y * b.y;
        return out;
    }

    public static Vector2f divide(final Vector2f a, final Vector2f b) {
        return Vector2f.divide(a, b, a);
    }

    public static Vector2f divide(final Vector2f a, final Vector2f b, final Vector2f out) {
        out.x = a.x / b.x;
        out.y = a.y / b.y;
        return out;
    }

}
