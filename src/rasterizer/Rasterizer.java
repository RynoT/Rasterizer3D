package rasterizer;

import rasterizer.target.RenderTarget;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Created by Ryan on 10/09/2017.
 */
public class Rasterizer {

    // Configuration for the rasterizer. Cannot be changed after the rasterizer has been made
    private final Config config;

    private final ForkJoinPool pool;
    private final RenderTarget backTarget;

    private long lastFrameTime = 0L;
    private float delta, fps;

    public Rasterizer(final int width, final int height) {
        this(width, height, new Config());
    }

    public Rasterizer(final int width, final int height, final Config config) {
        this.config = config;

        this.pool = new ForkJoinPool(Math.max(config.threadCount, 1));

        this.backTarget = new RenderTarget(width, height);
        this.backTarget.clear();
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

    public void render(final Graphics2D g2d) {
        if(this.lastFrameTime == 0L) {
            this.lastFrameTime = System.nanoTime();
        }

        // Render
        for(int j = 0; j < this.backTarget.getHeight(); j += this.config.segmentSize.height) {
            for(int i = 0; i < this.backTarget.getWidth(); i += this.config.segmentSize.width) {

            }
        }
        this.pool.execute(new RenderAction(0, 0, 0, 0));

        this.backTarget.render(g2d);

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

        // How large each render segment should be
        public Dimension segmentSize = new Dimension(50, 50);
    }

    private class RenderAction extends RecursiveAction {

        public RenderAction(final int x, final int y, final int width, final int height) {

        }

        @Override
        protected void compute() {
        }
    }
}
