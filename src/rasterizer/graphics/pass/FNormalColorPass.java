package rasterizer.graphics.pass;

import rasterizer.math.MathUtils;
import rasterizer.math.Vector3f;

/**
 * Created by Ryan on 16/09/2017.
 */
public class FNormalColorPass implements FragmentPass {

    @Override
    public boolean pass(final PassParameters params) {
        if(!params.inHasNormal) {
            return false;
        }
        final Vector3f normal = new Vector3f(params.finNormal).normalize();
        params.foutColor[0] = (params.foutColor[0] + MathUtils.clamp(normal.x < 0.0f ? -normal.x : normal.x, 0.0f, 1.0f)) * 0.5f;
        params.foutColor[1] = (params.foutColor[1] + MathUtils.clamp(normal.y < 0.0f ? -normal.y : normal.y, 0.0f, 1.0f)) * 0.5f;
        params.foutColor[2] = (params.foutColor[2] + MathUtils.clamp(normal.z < 0.0f ? -normal.z : normal.z, 0.0f, 1.0f)) * 0.5f;
        return true;
    }
}
