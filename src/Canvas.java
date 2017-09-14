import rasterizer.Rasterizer;
import rasterizer.graphics.layer.Layer3D;
import rasterizer.math.MathUtils;
import rasterizer.model.Model;
import rasterizer.model.mesh.basic.CubeMesh;
import rasterizer.model.mesh.basic.QuadMesh;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan on 10/09/2017.
 */
public class Canvas extends JFrame {

    public static final int WIDTH = 800, HEIGHT = 600;

    private final Rasterizer rasterizer;

    private Canvas(){
        final Rasterizer.Config config = new Rasterizer.Config();
        this.rasterizer = new Rasterizer(Canvas.WIDTH, Canvas.HEIGHT, config);

        final Layer3D layer = new Layer3D(Canvas.WIDTH, Canvas.HEIGHT);
        layer.flushDisplay = true;
        layer.setToPerspectiveProjection(45.0f, 1000.0f, 0.0001f);
        //layer.setToOrthographicProjection(1000.0f, 0.0001f);

        //final Model cubeModel = new Model(new CubeMesh(100.0f, 100.0f, 100.0f));
        final Model cubeModel = new Model(new QuadMesh(50.0f, 50.0f, true));
        //cubeModel.flushDisplay = true;
        cubeModel.flushPostRender = true;
        cubeModel.setPosition(100.0f, 100.0f, 0.0f);
        layer.addModel(cubeModel);

        final Model cubeModel2 = new Model(new QuadMesh(50.0f, 50.0f, true));
        cubeModel2.flushPostRender = true;
        cubeModel2.setPosition(200.0f, 100.0f, 0.0f);
        layer.addModel(cubeModel2);

        this.rasterizer.addLayer(layer);

        super.setResizable(false);
        super.setSize(Canvas.WIDTH, Canvas.HEIGHT);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel content = new JPanel(){
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);

                float dd = 10 * MathUtils.DEG_TO_RAD * rasterizer.getDelta();
                cubeModel.rotate(0.0f, 0.0f, dd);
                //cubeModel.setRotation(0,0,45*MathUtils.DEG_TO_RAD);

                Canvas.this.rasterizer.render((Graphics2D)g);
                Canvas.super.setTitle("FPS: " + String.format("%.1f", Canvas.this.rasterizer.getFps()));
            }
        };
        super.setContentPane(content);

        super.setLocationRelativeTo(null);

        new Thread(() -> {
            while(true){
                content.repaint();
                try {
                    Thread.sleep(5L);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(final String[] args){
        new Canvas().setVisible(true);
    }
}
