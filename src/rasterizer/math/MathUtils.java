package rasterizer.math;

/**
 * Created by Ryan on 27/02/2017.
 */
public final class MathUtils {

    public static final float PI = 3.14159265358979f;
    public static final float PI2 = MathUtils.PI * 2.0f;
    public static final float PI_HALF = MathUtils.PI / 2.0f;

    public static final float EPSILON = 0.00001f;

    public static final float RAD_TO_DEG = 180.0f / MathUtils.PI;
    public static final float DEG_TO_RAD = MathUtils.PI / 180.0f;

    private static final int SIN_MASK = ~(-1 << 14); //14 is accuracy
    private static final int SIN_COUNT = SIN_MASK + 1;

    private static final float[] SINE_TABLE;

    private MathUtils() {
    }

    public static int max(final int a, final int b) {
        return a > b ? a : b;
    }

    public static float max(final float a, final float b) {
        return a > b ? a : b;
    }

    public static int max(final int a, final int b, final int c) {
        return a > b ? (a > c ? a : c) : (b > c ? b : c);
    }

    public static float max(final float a, final float b, final float c) {
        return a > b ? (a > c ? a : c) : (b > c ? b : c);
    }

    public static int min(final int a, final int b) {
        return a < b ? a : b;
    }

    public static float min(final float a, final float b) {
        return a < b ? a : b;
    }

    public static int min(final int a, final int b, final int c) {
        return a < b ? (a < c ? a : c) : (b < c ? b : c);
    }

    public static float min(final float a, final float b, final float c) {
        return a < b ? (a < c ? a : c) : (b < c ? b : c);
    }

    public static int clamp(final int value, final int min, final int max) {
        return value < min ? min : value > max ? max : value;
    }

    public static float clamp(final float value, final float min, final float max) {
        return value < min ? min : value > max ? max : value;
    }

    public static int round(final float value) {
        return (int) (value > 0.0f ? (value + 0.5f) : (value - 0.5f));
    }

    public static boolean equals(final float a, final float b, final float epsilon) {
        return a == b || Math.abs(a - b) < epsilon;
    }

    public static float sin(final float radians) {
        return MathUtils.SINE_TABLE[(int) (radians * (MathUtils.SIN_COUNT / MathUtils.PI2)) & MathUtils.SIN_MASK];
    }

    public static float cos(final float radians) {
        // same as MathUtils.sin(radians + MathUtils.PI_HALF)
        return MathUtils.SINE_TABLE[(int) ((radians + MathUtils.PI_HALF) * (MathUtils.SIN_COUNT / MathUtils.PI2)) & MathUtils.SIN_MASK];
    }

    public static float tan(final float radians) {
        return (float) Math.tan(radians); //TODO
    }

    public static float sqrt(final float value) {
        return (float) Math.sqrt(value); //TODO
    }

    public static boolean equals(final float a, final float b) {
        return MathUtils.equals(a, b, MathUtils.EPSILON);
    }

    static {
        // Lookup table sourced from LibGDX
        SINE_TABLE = new float[MathUtils.SIN_COUNT];
        for(int i = 0; i < MathUtils.SIN_COUNT; i++) {
            SINE_TABLE[i] = (float) Math.sin((i + 0.5f) / MathUtils.SIN_COUNT * MathUtils.PI2);
        }
    }
}
