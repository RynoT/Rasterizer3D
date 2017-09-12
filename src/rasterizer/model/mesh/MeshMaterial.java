package rasterizer.model.mesh;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by Ryan Thomson on 11/01/2017.
 */
public class MeshMaterial {

    private final byte[] pixels;
    private final boolean alpha;
    private final int width, height;

    private final int stride;

    public MeshMaterial(final BufferedImage image) {
        assert image != null;
        assert image.getRaster().getDataBuffer() instanceof DataBufferByte;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.alpha = image.getColorModel().hasAlpha();
        this.pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        this.stride = this.alpha ? 4 : 3;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public byte[] getPixels() {
        return this.pixels;
    }

    public boolean hasAlpha() {
        return this.alpha;
    }

    // Return index within pixel array. xp and yp should be percentages.
    public int getIndex(float xp, float yp){
        final int x = (int) ((this.width - 1) * xp) % this.width,
                y = (int) ((this.height - 1) * yp) % this.height;
        return (x + y * this.width) * this.stride;
    }
}
