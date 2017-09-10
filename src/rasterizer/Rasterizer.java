package rasterizer;

import java.awt.*;
import java.util.concurrent.*;

/**
 * Created by Ryan on 10/09/2017.
 */
public class Rasterizer {

    // Configuration for the rasterizer. Cannot be changed after the rasterizer has been made
    private final Config config;

    private final ForkJoinPool pool;

    public Rasterizer() {
        this(new Config());
    }

    public Rasterizer(final Config config) {
        this.config = config;

        this.pool = new ForkJoinPool(Math.max(config.threadCount, 1));
    }

    public void shutdown() {
        this.pool.shutdown();
    }

    public void render(final Graphics2D g2d, final int width, final int height) {
        this.pool.execute(new RenderAction());


    }

    public static class Config {

        // FPS to render at (<= 0 is no cap)
        public int fps = 0;

        // Rendering thread count (<= 1 is 1)
        public int threadCount = Runtime.getRuntime().availableProcessors() - 1;
    }

    private class RenderAction extends RecursiveAction {

        @Override
        protected void compute() {
        }
    }
}
