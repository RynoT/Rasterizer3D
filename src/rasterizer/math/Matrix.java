package rasterizer.math;

import java.util.Arrays;

/**
 * Created by Ryan Thomson on 27/02/2017.
 */
public class Matrix {

    public static final ThreadLocal<Matrix> TEMP_4x1 = new LocalTempMatrix(4, 1);
    public static final ThreadLocal<Matrix> TEMP_4x4 = new LocalTempMatrix(4, 4);

    private final int rowCount, columnCount;
    private final float[] elements;

    public Matrix(final int rows, final int columns) {
        this(rows, columns, false);
    }

    public Matrix(final int rows, final int columns, final boolean identity) {
        this.rowCount = rows;
        this.columnCount = columns;

        this.elements = new float[rows * columns];
        if(identity){
            this.setIdentity();
        }
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public float[] getElements() {
        return this.elements;
    }

    public boolean isSquareMatrix() {
        return this.rowCount == this.columnCount;
    }

    public float getElement(final int index) {
        assert index >= 0 && index < this.elements.length;
        return this.elements[index];
    }

    public float getElement(final int row, final int column) {
        assert row >= 0 && row < this.rowCount;
        assert column >= 0 && column < this.columnCount;
        return this.elements[column + row * this.columnCount];
    }

    public void setElement(final int index, final float value) {
        assert index >= 0 && index < this.elements.length;
        this.elements[index] = value;
    }

    public void setElement(final int row, final int column, final float value) {
        assert row >= 0 && row < this.rowCount;
        assert column >= 0 && column < this.columnCount;
        this.elements[column + row * this.columnCount] = value;
    }

    public Matrix set(final Matrix matrix){
        assert matrix != null;
        return this.fill(matrix.elements);
    }

    public Matrix setIdentity() {
        return this.setDiagonal(1.0f);
    }

    public Matrix setDiagonal(final float scalar) {
        assert this.isSquareMatrix() : "Must be a square matrix";
        this.fill(0.0f);
        for(int i = 0; i < this.rowCount; i++) {
            this.elements[i + i * this.columnCount] = scalar;
        }
        return this;
    }

    // Get the determinant of a 2x2 or 3x3 matrix
    public float getDeterminant() {
        assert this.isSquareMatrix() && (this.rowCount == 2
                || this.rowCount == 3) : "Can only calculate determinant for 2x2 and 3x3";
        if(this.rowCount == 2) {
            //ad - bc
            return this.elements[0] * this.elements[3] - this.elements[1] * this.elements[2];
        }
        //a(ei - fh) - b(di - fg) + c(dh - eg)
        return this.elements[0] * (this.elements[4] * this.elements[8] - this.elements[5] * this.elements[7])
                - this.elements[1] * (this.elements[3] * this.elements[8] - this.elements[4] * this.elements[6])
                + this.elements[2] * (this.elements[3] * this.elements[7] - this.elements[2] * this.elements[6]);
    }

    public Matrix fill(final float scalar) {
        Arrays.fill(this.elements, scalar);
        return this;
    }

    public Matrix fill(final float[] data) {
        assert data.length == this.elements.length : "Expected: " + this.elements.length + ", Got: " + data.length;
        System.arraycopy(data, 0, this.elements, 0, data.length);
        return this;
    }

    public Matrix add(final Matrix target) {
        return this.add(target, this);
    }

    public Matrix add(final Matrix target, final Matrix out) {
        assert this.rowCount == target.rowCount && this.columnCount == target.columnCount;
        assert out.rowCount == this.rowCount && out.columnCount == this.columnCount;
        for(int i = 0; i < this.elements.length; i++) {
            out.elements[i] = this.elements[i] + target.elements[i];
        }
        return out;
    }

    public Matrix subtract(final Matrix target) {
        return this.add(target, this);
    }

    public Matrix subtract(final Matrix target, final Matrix out) {
        assert this.rowCount == target.rowCount && this.columnCount == target.columnCount;
        assert out.rowCount == this.rowCount && out.columnCount == this.columnCount;
        for(int i = 0; i < this.elements.length; i++) {
            out.elements[i] = this.elements[i] - target.elements[i];
        }
        return out;
    }

    public Matrix multiply(final Matrix target) {
        return this.multiply(target, this);
    }

    public Matrix multiply(final Matrix target, final Matrix out) {
        assert this.columnCount == target.rowCount;
        assert out.rowCount == this.rowCount && out.columnCount == target.columnCount;

        final float[] tmp = new float[out.elements.length];
        for(int j = 0; j < this.rowCount; j++) {
            for(int i = 0; i < target.columnCount; i++) {
                float value = 0.0f;
                for(int k = 0; k < this.columnCount; k++) {
                    value += this.elements[k + j * this.columnCount]
                            * target.elements[i + k * target.columnCount];
                }
                tmp[i + j * out.columnCount] = value;
            }
        }
        return out.fill(tmp);
    }

    public Matrix transpose() {
        return this.transpose(this);
    }

    public Matrix transpose(final Matrix out) {
        assert out.rowCount == this.columnCount && out.columnCount == this.rowCount;
        final float[] tmp = new float[this.elements.length];
        for(int i = 0; i < this.elements.length; i++) {
            final int x = i / this.columnCount;
            final int y = i % this.rowCount;
            tmp[y + x * out.columnCount] = this.elements[x + y * this.columnCount];
        }
        return out.fill(tmp);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < this.elements.length; i++) {
            sb.append(this.elements[i]);
            if(i == this.elements.length - 1) {
                break;
            }
            if((i + 1) % this.columnCount == 0) {
                sb.append("\n");
            } else {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // Set provided matrix to scale matrix. Returns provided matrix.
    public static Matrix setToScaleMatrix(final Matrix matrix, final Vector3f scale) {
        assert matrix.rowCount == 4 && matrix.columnCount == 4;
        return matrix.fill(new float[]{
                scale.x, 0.0f, 0.0f, 0.0f,
                0.0f, scale.y, 0.0f, 0.0f,
                0.0f, 0.0f, scale.z, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
    }

    // Set provided matrix to translate matrix. Returns provided matrix.
    public static Matrix setToTranslationMatrix(final Matrix matrix, final Vector3f translation) {
        assert matrix.rowCount == 4 && matrix.columnCount == 4;
        return matrix.fill(new float[]{
                1.0f, 0.0f, 0.0f, translation.x,
                0.0f, 1.0f, 0.0f, translation.y,
                0.0f, 0.0f, 1.0f, translation.z,
                0.0f, 0.0f, 0.0f, 1.0f
        });
    }

    // Set provided matrix to rotation matrix. Returns provided matrix.
    public static Matrix setToRotationMatrix(final Matrix matrix, final Vector3f rotation) {
        assert matrix.rowCount == 4 && matrix.columnCount == 4;
        // returns a combination of all three rotations around respected axis
        final float sx = MathUtils.sin(rotation.x), sy = MathUtils.sin(rotation.y), sz = MathUtils.sin(rotation.z);
        final float cx = MathUtils.cos(rotation.x), cy = MathUtils.cos(rotation.y), cz = MathUtils.cos(rotation.z);
        return matrix.fill(new float[]{cy * cz, -cy * sz, sy, 0.0f,
                sx * sy * cz + cx * sz, -sx * sy * sz + cx * cz, -sx * cy, 0.0f,
                -cx * sy * cz + sx * sz, cx * sy * sz + sx * cz, cx * cy, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
    }

    // Set provided matrix to rotation matrix. Returns provided matrix.
    public static Matrix setToRotationMatrix(final Matrix matrix, final float radians, final Vector3f axis) { //TODO quaternion?
        assert matrix.rowCount == 4 && matrix.columnCount == 4;
        final float s = MathUtils.sin(radians), c = MathUtils.cos(radians);
        final float omc = 1.0f - c;
        return matrix.fill(new float[]{
                axis.x * axis.x + (1.0f - axis.x * axis.x) * c, axis.x * axis.y * omc - axis.z * s, axis.x * axis.z * omc + axis.y * s, 0.0f,
                axis.x * axis.y * omc + axis.z * s, axis.y * axis.y + (1.0f - axis.y * axis.y) * c, axis.y * axis.z * omc - axis.x * s, 0.0f,
                axis.x * axis.z * omc - axis.y * s, axis.y * axis.z * omc + axis.x * s, axis.z * axis.z + (1.0f - axis.z * axis.z) * c, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        });
    }

    // Set provided matrix to an orthographic matrix. Returns provided matrix.
    public static Matrix setToOrthographicMatrix(final Matrix matrix, final float width,
                                                 final float height, final float far, final float near) {
        return Matrix.setToOrthographicMatrix(matrix, 0.0f, width, 0.0f, height, far, near);
    }

    // Set provided matrix to an orthographic matrix. Returns provided matrix.
    public static Matrix setToOrthographicMatrix(final Matrix matrix, final float left, final float right,
                                                 final float top, final float bottom, final float far, final float near) {
        assert matrix.rowCount == 4 && matrix.columnCount == 4;
        // Configured so that top left is (0,0) instead of bottom left. This is to work with java graphics.
        return matrix.fill(new float[]{
                2.0f / (right - left), 0.0f, 0.0f, -(right + left) / (right - left),
                0.0f, 2.0f / (bottom - top), 0.0f, -(top + bottom) / (bottom - top),
                0.0f, 0.0f, -2.0f / (far - near), -(far + near) / (far - near),
                0.0f, 0.0f, 0.0f, 1.0f
        });
//        return matrix.fill(new float[]{
//                2.0f / (width - 1.0f), 0.0f, 0.0f, -1.0f,
//                0.0f, -2.0f / (height - 1.0f), 0.0f, 1.0f,
//                0.0f, 0.0f, 2.0f / (far - near), (near + far) / (near - far),
//                0.0f, 0.0f, 0.0f, 1.0f
//        });
    }

    // Set provided matrix to a perspective matrix. Returns provided matrix.
    public static Matrix setToPerspectiveMatrix(final Matrix matrix, final float width, final float height,
                                                final float fov, final float far, final float near) {
        assert matrix.rowCount == 4 && matrix.columnCount == 4;
        final float aspect = width / height;
        final float tfov = 1.0f / MathUtils.tan((fov / 2.0f) * MathUtils.DEG_TO_RAD);
        return matrix.fill(new float[]{
                tfov / aspect, 0.0f, 0.0f, 0.0f,
                0.0f, tfov, 0.0f, 0.0f,
                0.0f, 0.0f, -(far + near) / (far - near), -1.0f,
                0.0f, 0.0f, -(2.0f * far * near) / (far - near), 0.0f
        });
    }

    private static class LocalTempMatrix extends ThreadLocal<Matrix> {

        private final Matrix temp;

        private LocalTempMatrix(final int rows, final int columns){
            this.temp = new Matrix(rows, columns);
        }

        @Override
        protected Matrix initialValue() {
            return this.temp;
        }
    }
}
