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

    public Rasterizer(final int width, final int height) {
        this(width, height, new Config());
    }

    public Rasterizer(final int width, final int height, final Config config) {
        this.config = config;

        this.pool = new ForkJoinPool(Math.max(config.threadCount, 1));

        this.backTarget = new RenderTarget(width, height);
        this.backTarget.clear();
    }

    public void shutdown() {
        this.pool.shutdown();
    }

    public void render(final Graphics2D g2d) {
        for(int j = 0; j < this.backTarget.getHeight(); j += this.config.segmentSize.height) {
            for(int i = 0; i < this.backTarget.getWidth(); i += this.config.segmentSize.width) {

            }
        }
        this.pool.execute(new RenderAction(0,0,0,0));

        this.backTarget.render(g2d);
    }

    public static class Config {

        // FPS to render at (<= 0 is no cap)
        public int fps = 0;

        // Rendering thread count (<= 1 is 1)
        public int threadCount = Runtime.getRuntime().availableProcessors() - 1;

        // How large each render segment should be
        public Dimension segmentSize = new Dimension(50, 50);
    }

    private class RenderAction extends RecursiveAction {

        public RenderAction(final int x, final int y, final int width, final int height){

        }

        @Override
        protected void compute() {
        }
    }
}
