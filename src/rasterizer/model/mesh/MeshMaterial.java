package rasterizer.model.mesh;

import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Ryan Thomson on 11/01/2017.
 */
public class MeshMaterial {

    private final float[] pixels;
    private final boolean hasAlpha;
    private final int width, height;

    private final int stride;

    public MeshMaterial(final BufferedImage image) {
        assert image != null;

        this.width = image.getWidth();
        this.height = image.getHeight();
        this.hasAlpha = image.getColorModel().hasAlpha();
        this.stride = this.hasAlpha ? 4 : 3;

        final DataBuffer dataBuffer = image.getRaster().getDataBuffer();

        ByteBuffer byteBuffer;
        int pixelLen = 1;
        if(dataBuffer instanceof DataBufferByte) {
            final byte[] pixelData = ((DataBufferByte) dataBuffer).getData();
            byteBuffer = ByteBuffer.wrap(pixelData);
        } else if(dataBuffer instanceof DataBufferUShort) {
            final short[] pixelData = ((DataBufferUShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * (pixelLen = 2));
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        } else if(dataBuffer instanceof DataBufferShort) {
            final short[] pixelData = ((DataBufferShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * (pixelLen = 2));
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        } else if(dataBuffer instanceof DataBufferInt) {
            final int[] pixelData = ((DataBufferInt) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * (pixelLen = 4));
            byteBuffer.asIntBuffer().put(IntBuffer.wrap(pixelData));
        } else {
            throw new IllegalArgumentException("Invalid buffered image buffer type");
        }

        // Convert byte array to float array
        final byte[] array = byteBuffer.array();
        this.pixels = new float[array.length / pixelLen];
        for(int i = 0, k = 0; k < array.length; i++) {
            int color = 0x0;
            for(int j = 0; j < pixelLen; j++) {
                color |= (array[k++] & 0xff) << (j * 8);
            }
            this.pixels[i] = (color & 0xff) / 255.0f;
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public float[] getPixels() {
        return this.pixels;
    }

    public boolean hasAlpha() {
        return this.hasAlpha;
    }

    // Return index within pixel array. xp and yp should be percentages.
    public int getIndex(float xp, float yp) {
        final int x = (int) ((this.width - 1) * xp) % this.width,
                y = (int) ((this.height - 1) * yp) % this.height;
        return (x + y * this.width) * this.stride;
    }
}
