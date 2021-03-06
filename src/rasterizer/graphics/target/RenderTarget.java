package rasterizer.graphics.target;

import rasterizer.math.MathUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by Ryan on 11/09/2017.
 */
public class RenderTarget extends BufferedImage {

    public static final int RGBA_FLOAT_LENGTH = 4; // r, g, b, a

    public boolean _render_async = true; // It is up to subclasses to implement async
    public boolean _display_flush = false;
    public boolean _display_chunks = false;
    public boolean _disable_custom_rgba = false;
    public float _render_chunk_size = 0.25f; // Percentage size of each chunk (0.0f - 1.0f both inclusive). Percentage is of screen dimensions.

    private final float[] data;
    private final float[] clearColor = new float[]{5.0f / 255.0f, 10.0f / 255.0f, 15.0f / 255.0f, 1.0f};

    private boolean flushed = false;
    private final Rectangle flushBounds;

    protected final Graphics2D graphics;

    public RenderTarget(final int width, final int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);

        assert width > 0 && height > 0 : "Width and Height must both be > 0";
        this.data = new float[width * height * RenderTarget.RGBA_FLOAT_LENGTH];
        this.flushBounds = new Rectangle(0, 0, width, height);

        this.graphics = super.createGraphics();
    }

    public void dirty() {
        this.flushed = false;
        this.flushBounds.setBounds(0, 0, super.getWidth(), super.getHeight());
    }

    public void setClearColor(final float[] rgba) {
        assert rgba != null && rgba.length == 4;
        System.arraycopy(rgba, 0, this.clearColor, 0, rgba.length);
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

    public void prepareRGBA(final int maxX, final int maxY, final int minX, final int minY) {
        this.prepareRGBA(MathUtils.min(maxX, super.getWidth() - 1), MathUtils.min(maxY, super.getHeight() - 1));
        this.prepareRGBA(MathUtils.max(minX, 0), MathUtils.max(minY, 0));
    }

    public void prepareRGBA(final int x, final int y) {
        if(x < 0 || x >= super.getWidth() || y < 0 || y >= super.getHeight()) {
            return;
        }
        if(this.flushed) {
            this.flushBounds.setBounds(x, y, 1, 1);
            this.flushed = false;
        } else if(x < this.flushBounds.x || x >= this.flushBounds.x + this.flushBounds.width
                || y < this.flushBounds.y || y >= this.flushBounds.y + this.flushBounds.height) {
            int x1 = this.flushBounds.x, y1 = this.flushBounds.y;
            int x2 = x1 + this.flushBounds.width, y2 = y1 + this.flushBounds.height;

            if(x1 > x) {
                x1 = x;
            }
            if(y1 > y) {
                y1 = y;
            }
            if(x2 < x) {
                x2 = x;
            }
            if(y2 < y) {
                y2 = y;
            }
            this.flushBounds.setBounds(x1, y1, x2 - x1, y2 - y1);
        }
    }

    public void setRGBA(final float[] rgba, final int x, final int y) {
        final int index = (x + y * super.getWidth()) * RenderTarget.RGBA_FLOAT_LENGTH;
        this.data[index] = rgba[0];
        this.data[index + 1] = rgba[1];
        this.data[index + 2] = rgba[2];
        this.data[index + 3] = rgba[3];
    }

    public void clear() {
        this.clear(this.clearColor);
    }

    public void clear(final float[] rgba) {
        assert rgba != null && rgba.length == 4;
        for(int i = 0; i < this.data.length; i += RenderTarget.RGBA_FLOAT_LENGTH) {
            System.arraycopy(rgba, 0, this.data, i, rgba.length);
        }
        this.flushed = true;

        this.graphics.setBackground(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));
        this.graphics.clearRect(0, 0, super.getWidth(), super.getHeight());
    }

    public void flushRGBA() {
        this.flushRGBA(this._display_flush);
    }

    public void flushRGBA(final boolean flushDisplay) {
        if(!this.flushed && !this._disable_custom_rgba) {
            Object pixel = null;
            final ColorModel model = super.getColorModel();
            final WritableRaster raster = super.getRaster();

            final int x = this.flushBounds.x, y = this.flushBounds.y, w = this.flushBounds.width, h = this.flushBounds.height;

            for(int j = y; j < y + h; j++) {
                if(j >= super.getHeight()) {
                    break;
                }
                for(int i = x; i < x + w; i++) {
                    if(i >= super.getWidth()) {
                        break;
                    }
                    final int k = (i + j * super.getWidth()) * RenderTarget.RGBA_FLOAT_LENGTH;
                    if(k >= this.data.length) {
                        break;
                    }
                    final int r = (int) (this.data[k] * 255.0f), g = (int) (this.data[k + 1] * 255.0f),
                            b = (int) (this.data[k + 2] * 255.0f), a = (int) (this.data[k + 3] * 255.0f);
                    final int argb = (a << 24) | (r << 16) | (g << 8) | (b);
                    // Set pixel color. Don't bother with #setRGB to avoid needlessly acquiring the images monitor every call.
                    raster.setDataElements(i, j, pixel = model.getDataElements(argb, pixel));
                }
            }
            this.flushed = true;

            if(flushDisplay || this._display_flush) {
                this.graphics.setColor(Color.RED);
                this.graphics.drawRect(x, y, w, h);
            }
            if(this._display_chunks) {
                this.graphics.setColor(Color.ORANGE);

                final int dw = (int)(super.getWidth() * this._render_chunk_size);
                for(int i = dw; i < super.getWidth(); i += dw) {
                    this.graphics.drawLine(i, 0, i, super.getHeight());
                }
                final int dh = (int)(super.getHeight() * this._render_chunk_size);
                for(int j = dh; j < super.getHeight(); j += dh) {
                    this.graphics.drawLine(0, j, super.getWidth(), j);
                }
            }
        }
    }

    public void render(final Graphics2D g2d) {
        this.flushRGBA();
        g2d.drawImage(this, 0, 0, null);
    }
}