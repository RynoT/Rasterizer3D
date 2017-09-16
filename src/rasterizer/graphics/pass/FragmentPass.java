package rasterizer.graphics.pass;

/**
 * Created by Ryan on 14/09/2017.
 */
public abstract class FragmentPass {

    public boolean _on_fail_return = false;

    public abstract boolean pass(final PassParameters params);

    public static FragmentPass chain(final FragmentPass... passes) {
        return new Chain(passes);
    }

    private static class Chain extends FragmentPass {

        private final FragmentPass[] passes;

        private Chain(final FragmentPass... passes) {
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
}
