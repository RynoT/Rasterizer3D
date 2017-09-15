import rasterizer.Rasterizer;
import rasterizer.graphics.layer.Layer3D;
import rasterizer.graphics.pass.FColorPass;
import rasterizer.graphics.pass.FTexturePass;
import rasterizer.graphics.pass.FragmentPass;
import rasterizer.math.MathUtils;
import rasterizer.model.Model;
import rasterizer.model.mesh.MeshMaterial;
import rasterizer.model.mesh.basic.CubeMesh;
import rasterizer.model.mesh.basic.QuadMesh;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Ryan on 10/09/2017.
 */
public class Canvas extends JFrame {

    public static final int WIDTH = 800, HEIGHT = 600;

    private final Rasterizer rasterizer;

    private Canvas() {
        final FragmentPass texturePass = new FTexturePass();

        final Rasterizer.Config config = new Rasterizer.Config();
        this.rasterizer = new Rasterizer(Canvas.WIDTH, Canvas.HEIGHT, config);

        final Layer3D layer = new Layer3D(Canvas.WIDTH, Canvas.HEIGHT);
        //layer.flushDisplay = true;
        //layer.setToPerspectiveProjection(45.0f, 1000.0f, 0.0001f);
        layer.setToOrthographicProjection(1000.0f, 0.0001f);

        final Model cubeModel = new Model(new CubeMesh(300.0f, 300.0f, 300.0f));
        //final Model cubeModel = new Model(new QuadMesh(500, 500, true));
        try {
            cubeModel.getMesh().setMaterial(new MeshMaterial(ImageIO.read(Canvas.class.getResource("media/crate2.png"))));
        } catch(IOException e) {
            e.printStackTrace();
        }
        //cubeModel.flushDisplay = true;
        cubeModel.flushPostRender = true;
        cubeModel.setPosition(794 / 2, 571 / 2, 200.0f);
        cubeModel.getMesh().setFragmentPass(texturePass);
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
                cubeModel.rotate(dd, dd,0);
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
                    Thread.sleep(5L);
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
