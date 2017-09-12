import rasterizer.Rasterizer;
import rasterizer.graphics.layer.Layer3D;
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
        layer.setToPerspectiveProjection(45.0f, 1000.0f, 0.0001f);
        //layer.setToOrthographicProjection(1000.0f, 0.0001f);

//        final Model quadModel = new Model(new QuadMesh(100.0f, 100.0f, false));
//        quadModel.setPosition(10.0f, 10.0f, 0.0f);
//        layer.addModel(quadModel);

        final Model cubeModel = new Model(new CubeMesh(100.0f, 100.0f, 100.0f));
        cubeModel.setPosition(100.0f, 100.0f, 0.0f);
        layer.addModel(cubeModel);

        this.rasterizer.addLayer(layer);

        super.setResizable(false);
        super.setSize(Canvas.WIDTH, Canvas.HEIGHT);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel content = new JPanel(){
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);

                Canvas.this.rasterizer.render((Graphics2D)g);
                Canvas.super.setTitle("FPS: " + String.format("%.1f", Canvas.this.rasterizer.getFps()));
            }
        };
        super.setContentPane(content);

        super.setLocationRelativeTo(null);

        new Thread(() -> {
            while(true){
                cubeModel.rotate(0.0f, 0.1f, 0.0f);
                content.repaint();
            }
        }).start();
    }

    public static void main(final String[] args){
        new Canvas().setVisible(true);
    }
}
