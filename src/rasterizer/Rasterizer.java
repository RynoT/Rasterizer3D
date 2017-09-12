package rasterizer;

import rasterizer.graphics.layer.Layer;
import rasterizer.graphics.target.RenderTarget;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Ryan on 10/09/2017.
 */
public class Rasterizer {

    // Configuration for the rasterizer. Cannot be changed after the rasterizer has been made
    private final Config config;

    private final ForkJoinPool pool;
    private final BufferedImage buffer;
    private Color backgroundColor = new Color(5, 10, 15);

    private long lastFrameTime = 0L;
    private float delta, fps;

    private final List<Layer> layers = new ArrayList<>();

    public Rasterizer(final int width, final int height) {
        this(width, height, new Config());
    }

    public Rasterizer(final int width, final int height, final Config config) {
        this.config = config;

        this.pool = new ForkJoinPool(Math.max(config.threadCount, 1));
        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public float getFps() {
        return this.fps;
    }

    public float getDelta() {
        return this.delta;
    }

    public void shutdown() {
        this.pool.shutdown();
    }

    public void addLayer(final Layer layer) {
        this.layers.add(layer);
    }

    public void removeLayer(final Layer layer) {
        this.layers.remove(layer);
    }

    public void setBackgroundColor(final Color color) {
        this.backgroundColor = color;
    }

    public void render(final Graphics2D g2d) {
        if(this.lastFrameTime == 0L) {
            this.lastFrameTime = System.nanoTime();
        }

        // Render
        final Graphics2D bg2d = this.buffer.createGraphics();
        bg2d.setBackground(this.backgroundColor);
        bg2d.clearRect(0, 0, this.buffer.getWidth(), this.buffer.getHeight());

        final RenderAction[] actions = new RenderAction[this.layers.size()];
        for(int i = 0; i < this.layers.size(); i++) {
            this.pool.execute(actions[i] = new RenderAction(this.layers.get(i)));
        }
        // Wait for render to complete and then draw to back
        for(final RenderAction action : actions) {
            action.join();

            action.layer.render(bg2d);
        }

        //TODO ditch the back buffer
        bg2d.dispose();
        g2d.drawImage(this.buffer, 0, 0, null);

        // Calculate delta
        long time = System.nanoTime();
        long frameDuration = time - this.lastFrameTime;
        if(this.config.fps > 0) {
            final float requested = 1000.0f / this.config.fps; //millis
            final float difference = requested - (frameDuration * 1e-6f);
            if(difference > 0.0f) {
                final long millis = (long) difference;
                final int nanos = (int) ((difference - millis) * 1e6f);
                try {
                    Thread.sleep(millis, nanos);
                } catch(final InterruptedException ignored) {
                }
                // After sleep, update the frame time to accommodate for it
                frameDuration += millis * 1e6f + nanos;
                time = System.nanoTime();
            }
        }
        this.delta = frameDuration * 1e-9f;
        this.fps = 1.0f / this.delta;
        this.lastFrameTime = time;
    }

    public static class Config {

        // FPS to render at (<= 0 is no cap)
        public int fps = 60;

        // Rendering thread count (<= 1 is 1)
        public int threadCount = Runtime.getRuntime().availableProcessors() - 1;
    }

    private class RenderAction extends RecursiveAction {

        private final Layer layer;

        RenderAction(final Layer layer) {
            this.layer = layer;
        }

        @Override
        protected void compute() {
            this.layer.render();
        }
    }
}
