package rasterizer.graphics.pass;

import java.util.Arrays;

/**
 * Created by Ryan on 15/09/2017.
 */
public interface VertexPass {

    void pass(final PassParameters params);

    static VertexPass chain(final VertexPass... passes) {
        assert passes != null;
        class Chain implements VertexPass {

            private final VertexPass[] passes;

            private Chain(final VertexPass[] passes) {
                this.passes = passes;
            }

            @Override
            public void pass(final PassParameters params) {
                Arrays.stream(this.passes).forEach((pass) -> pass.pass(params));
            }
        }
        return new Chain(passes);
    }
}
