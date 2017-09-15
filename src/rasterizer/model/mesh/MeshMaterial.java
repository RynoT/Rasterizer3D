package rasterizer.model.mesh;

import java.awt.*;
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Ryan on 12/09/2017.
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

        // The image itself could be any valid buffered image type.
        // Instead of trying to validate each type we are just going to convert it before getting the pixel data.
        BufferedImage img;
        if((this.hasAlpha && image.getType() == BufferedImage.TYPE_INT_ARGB) || image.getType() == BufferedImage.TYPE_INT_RGB) {
            img = image;
        } else {
            img = new BufferedImage(image.getWidth(), image.getHeight(), this.hasAlpha ?
                    BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            final Graphics2D g2d = img.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
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

        // No buffered images are stored in the material. Only a copy of the raw pixel data.
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
        xp %= 1.0f;
        yp %= 1.0f;
        if(xp < 0.0f) {
            xp = 1.0f + xp;
        }
        if(yp < 0.0f) {
            yp = 1.0f + yp;
        }
        final int x = (int) ((this.width - 1) * xp) % this.width,
                y = (int) ((this.height - 1) * yp) % this.height;
        return (x + y * this.width) * this.stride;
    }
}
