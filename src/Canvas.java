import rasterizer.Rasterizer;

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

        super.setResizable(false);
        super.setSize(Canvas.WIDTH, Canvas.HEIGHT);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel content = new JPanel(){
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);

                Canvas.this.rasterizer.render((Graphics2D)g);
            }
        };
        super.setContentPane(content);

        super.setLocationRelativeTo(null);

        new Thread(() -> {
            while(true){
                content.repaint();
            }
        }).start();
    }

    public static void main(final String[] args){
        new Canvas().setVisible(true);
    }
}
