package rasterizer.graphics.pass;

/**
 * Created by Ryan on 14/09/2017.
 */
public abstract class FragmentPass {

    public abstract boolean pass(final PassParameters params);
}
