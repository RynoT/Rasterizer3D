package rasterizer.graphics.pass;

import rasterizer.graphics.light.Light;
import rasterizer.math.MathUtils;
import rasterizer.math.Vector3f;

/**
 * Created by Ryan on 15/09/2017.
 */
public class FLightPass implements FragmentPass {

    private final Light[] lights;

    public FLightPass(final int lightCount) {
        this.lights = new Light[lightCount];
    }

    public int getLightCount() {
        return this.lights.length;
    }

    public Light getLight(final int index) {
        return this.lights[index];
    }

    public void setLight(final int index, final Light light) {
        this.lights[index] = light;
    }

    @Override
    public boolean pass(final PassParameters params) {
        if(params._skip_lighting) {
            return true;
        }
        if(!params.inHasNormal) {
            return false;
        }
        final Vector3f normal = new Vector3f(params.finNormal).normalize();
        final Vector3f inPoint = Vector3f.multiply(new Vector3f(params.finPointUnprojected), params.voutMatrixVM);
        for(final Light light : this.lights) {
            if(light == null) {
                continue;
            }
            final float[] lightColor = light.getColor();

            final Vector3f lightDir = new Vector3f();
            Vector3f.multiply(light.getPosition(), params.vinMatrixV, lightDir);
            Vector3f.subtract(lightDir, inPoint);
            lightDir.normalize();

            final float intensity = -Vector3f.dot(normal, lightDir);
            if(intensity > 0.0f) {
                params.foutColor[0] *= lightColor[0] * intensity;
                params.foutColor[1] *= lightColor[1] * intensity;
                params.foutColor[2] *= lightColor[2] * intensity;
            }
        }
        params.foutColor[0] = MathUtils.clamp(params.foutColor[0], 0.0f, 1.0f);
        params.foutColor[1] = MathUtils.clamp(params.foutColor[1], 0.0f, 1.0f);
        params.foutColor[2] = MathUtils.clamp(params.foutColor[2], 0.0f, 1.0f);

        return true;
    }
}
