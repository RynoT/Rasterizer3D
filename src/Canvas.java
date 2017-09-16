import rasterizer.Rasterizer;
import rasterizer.graphics.layer.Layer3D;
import rasterizer.graphics.light.PointLight;
import rasterizer.graphics.pass.FLightPass;
import rasterizer.graphics.pass.FNormalColorPass;
import rasterizer.graphics.pass.FTexturePass;
import rasterizer.graphics.pass.FragmentPass;
import rasterizer.math.MathUtils;
import rasterizer.math.Vector3f;
import rasterizer.model.Model;
import rasterizer.model.mesh.MeshMaterial;
import rasterizer.model.mesh.basic.CubeMesh;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Ryan on 10/09/2017.
 */
public class Canvas extends JFrame {

    public static final int WIDTH = 800, HEIGHT = 600;

    private final Rasterizer rasterizer;

    private Canvas() {
        final FTexturePass texturePass = new FTexturePass();
        texturePass._on_fail_return = true;

        final FLightPass lightPass = new FLightPass(1);
        lightPass.setLight(0, new PointLight(new Vector3f(0.0f, 0.0f, 0.0f)).setColor(new float[]{1.0f, 0.0f, 0.0f, 1.0f}));

        final FragmentPass fragPass = FragmentPass.chain(texturePass, lightPass);

        this.rasterizer = new Rasterizer(Canvas.WIDTH, Canvas.HEIGHT);

        final Layer3D layer = new Layer3D(Canvas.WIDTH, Canvas.HEIGHT);
        //layer._display_flush = true;
        //layer._display_chunks = true;
        //layer.setToPerspectiveProjection(45.0f, 100000.0f, 0.1f);
        layer.setToOrthographicProjection(1000.0f, 0.1f);

        final Model cubeModel = new Model(new CubeMesh(300.0f, 300.0f, 300.0f));
        //final Model cubeModel = new Model(new QuadMesh(500, 500, true));
        try {
            cubeModel.getMesh().setMaterial(new MeshMaterial(ImageIO.read(Canvas.class.getResource("media/crate2.png"))));
        } catch(IOException e) {
            e.printStackTrace();
        }
        cubeModel.setPosition(794 / 2, 571 / 2, 250.0f);
        cubeModel.getMesh().setFragmentPass(fragPass);
        layer.addModel(cubeModel);

        this.rasterizer.addLayer(layer);

        super.setResizable(false);
        super.setSize(Canvas.WIDTH, Canvas.HEIGHT);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel content = new JPanel() {
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);

                float dd = 25 * MathUtils.DEG_TO_RAD * rasterizer.getDelta();
                cubeModel.rotate(dd, dd, 0);
//                cubeModel.translate(0,0,-120*rasterizer.getDelta());
                //cubeModel.setRotation(-60*MathUtils.DEG_TO_RAD,0,0);

                Canvas.this.rasterizer.render((Graphics2D) g);
                Canvas.super.setTitle(String.format("FPS: %02d | %02.1f", Math.round(Canvas
                        .this.rasterizer.getAverageFps()), Canvas.this.rasterizer.getFps()));
            }
        };
        super.setContentPane(content);

        super.setLocationRelativeTo(null);

        new Thread(() -> {
            while(true) {
                content.repaint();
                try {
                    Thread.sleep(4L);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(final String[] args) {
        new Canvas().setVisible(true);
    }
}
