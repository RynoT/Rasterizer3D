package rasterizer.math;

/**
 * Created by Ryan on 27/02/2017.
 */
public class Vector2f {

    float x, y;

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

//    public Vector2f add(final Vector2f target){
//        return this.add(target, this);
//    }
//
//    public Vector2f add(final Vector2f target, final Vector2f out){
//        return out.setElement(this.x + target.x, this.y + target.y);
//    }
//
//    public Vector2f subtract(final Vector2f target){
//        return this.subtract(target, this);
//    }
//
//    public Vector2f subtract(final Vector2f target, final Vector2f out){
//        return out.setElement(this.x - target.x, this.y - target.y);
//    }
//
//    public Vector2f multiply(final Vector2f target){
//        return this.multiply(target, this);
//    }
//
//    public Vector2f multiply(final Vector2f target, final Vector2f out){
//        return out.setElement(this.x * target.x, this.y * target.y);
//    }
//
//    public Vector2f divide(final Vector2f target){
//        return this.divide(target, this);
//    }
//
//    public Vector2f divide(final Vector2f target, final Vector2f out){
//        return out.setElement(this.x / target.x, this.y / target.y);
//    }
}
