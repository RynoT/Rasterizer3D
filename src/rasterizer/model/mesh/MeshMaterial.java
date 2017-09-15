package rasterizer.model.mesh;

import java.awt.*;
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Ryan Thomson on 11/01/2017.
 */
public class MeshMaterial {

    private final float[] pixels; //rgba
    private final boolean hasAlpha;
    private final int width, height;

    private final int stride;

    public MeshMaterial(final BufferedImage image) {
        assert image != null;

        this.width = image.getWidth();
        this.height = image.getHeight();
        this.hasAlpha = image.getColorModel().hasAlpha();
        this.stride = this.hasAlpha ? 4 : 3;

        // The image itself could be any valid type.
        // Instead of trying to validate each type we are just going to convert it before getting the pixel data.
        BufferedImage img;
        if((this.hasAlpha && image.getType() == BufferedImage.TYPE_INT_ARGB) || image.getType() == BufferedImage.TYPE_INT_RGB) {
            img = image;
        } else {
            img = new BufferedImage(image.getWidth(), image.getHeight(), this.hasAlpha ?
                    BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            img.getGraphics().drawImage(image, 0, 0, null);
        }
        assert img.getRaster().getDataBuffer() instanceof DataBufferInt : img.getRaster().getDataBuffer();

        // Convert pixel byte array to float array
        final int[] array = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        this.pixels = new float[array.length * this.stride];

        final float inv255 = 1.0f / 255.0f;
        for(int i = 0, k = 0; i < array.length; i++) {
            this.pixels[k++] = ((array[i] >> 16) & 0xff) * inv255; //r
            this.pixels[k++] = ((array[i] >> 8) & 0xff) * inv255;  //g
            this.pixels[k++] = (array[i] & 0xff) * inv255;         //b
            if(!this.hasAlpha) {
                continue;
            }
            this.pixels[k++] = ((array[i] >> 24) & 0xff) * inv255; //a
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
        if(xp < 0.0f) {
            return this.getIndex(1.0f + xp, yp);
            //xp = -xp;
        }
        if(yp < 0.0f) {
            return this.getIndex(xp, 1.0f + yp);
        }
        if(xp > 1.0f) {
            xp %= 1.0f;
        }
        if(yp > 1.0f) {
            yp %= 1.0f;
        }
        final int x = (int) ((this.width - 1) * xp) % this.width,
                y = (int) ((this.height - 1) * yp) % this.height;
        return (x + y * this.width) * this.stride;
    }
}
