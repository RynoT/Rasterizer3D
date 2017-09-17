package rasterizer.graphics.pass;

/**
 * Created by Ryan on 14/09/2017.
 */
public interface FragmentPass {

    boolean pass(final PassParameters params);

    static FragmentPass chain(final FragmentPass... passes) {
        assert passes != null;
        class Chain implements FragmentPass {

            private final FragmentPass[] passes;

            private Chain(final FragmentPass[] passes) {
                this.passes = passes;
            }

            @Override
            public boolean pass(final PassParameters params) {
                for(final FragmentPass pass : this.passes) {
                    if(!pass.pass(params)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return new Chain(passes);
    }
}
