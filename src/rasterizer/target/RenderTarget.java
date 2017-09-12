package rasterizer.target;

import com.sun.org.apache.regexp.internal.RE;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by Ryan on 11/09/2017.
 */
public class RenderTarget extends BufferedImage {

    public static final int RGBA_FLOAT_LENGTH = 4; // r, g, b, a

    private final float[] data;
    private final float[] clearColor = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    private boolean flushed = false;

    public RenderTarget(final int width, final int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);

        assert width > 0 && height > 0 : "Width and Height must both be > 0";
        this.data = new float[width * height * RenderTarget.RGBA_FLOAT_LENGTH];
    }

    public static Color asColor(final float[] rgba) {
        assert rgba != null && rgba.length == 4;
        return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public float[] getRGBA(final int x, final int y) {
        return this.getRGBA(x, y, new float[RenderTarget.RGBA_FLOAT_LENGTH]);
    }

    public float[] getRGBA(final int x, final int y, final float[] out) {
        assert x >= 0 && x < super.getWidth();
        assert y >= 0 && y < super.getHeight();
        assert out != null && out.length == 4;
        System.arraycopy(this.data, (x + y * super.getWidth())
                * RenderTarget.RGBA_FLOAT_LENGTH, out, 0, out.length);
        this.flushed = false;
        return out;
    }

    public void setClearColor(final float[] rgba) {
        assert rgba != null && rgba.length == 4;
        System.arraycopy(rgba, 0, this.clearColor, 0, rgba.length);
    }

    public void setRGBA(final float[] rgba, final int x, final int y) {
        assert rgba != null && rgba.length == 4;
        assert x >= 0 && x < super.getWidth();
        assert y >= 0 && y < super.getHeight();
        System.arraycopy(rgba, 0, this.data, (x + y * super.getWidth()) * RenderTarget.RGBA_FLOAT_LENGTH, rgba.length);
        this.flushed = false;
    }

    public void clear() {
        this.clear(this.clearColor);
    }

    public void clear(final float[] rgba) {
        assert rgba != null && rgba.length == 4;
        for(int i = 0; i < this.data.length; i += RenderTarget.RGBA_FLOAT_LENGTH) {
            System.arraycopy(rgba, 0, this.data, i, rgba.length);
        }
        this.flushed = false;
    }

    public void render(final Graphics2D g2d) {
        // Check to see if our buffered image contains the latest color data
        if(!this.flushed) {
            Object pixel = null;
            final ColorModel model = super.getColorModel();
            final WritableRaster raster = super.getRaster();
            for(int k = 0, w = 0; k < this.data.length; k += RenderTarget.RGBA_FLOAT_LENGTH, w++){
                final int i = w % super.getWidth(), j = w / super.getWidth();
                final int argb = ((int) (this.data[k + 3] * 255.0f) << 24) | ((int) (this.data[k] * 255.0f) << 16)
                        | ((int) (this.data[k + 1] * 255.0f) << 8) | ((int) (this.data[k + 2] * 255.0f));
                // Set pixel color. Don't bother with #setRGB to avoid needlessly acquiring the images monitor every call.
                raster.setDataElements(i, j, pixel = model.getDataElements(argb, pixel));
            }
            // Optimization variable. We only need to flush to the raster if changes have been made, otherwise we can achieve the same effect by doing nothing.
            this.flushed = true;
        }
        g2d.drawImage(this, 0, 0, null);
    }
}