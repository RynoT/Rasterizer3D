import rasterizer.Rasterizer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ryan on 10/09/2017.
 */
public class Canvas extends JFrame {

    private final Rasterizer rasterizer;

    private Canvas(){
        final Rasterizer.Config config = new Rasterizer.Config();
        this.rasterizer = new Rasterizer(config);

        super.setResizable(false);
        super.setSize(800, 600);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel content = new JPanel(){
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);

                Canvas.this.rasterizer.render((Graphics2D)g, super.getWidth(), super.getHeight());
            }
        };
        super.setContentPane(content);

        super.setLocationRelativeTo(null);

        new Thread(() -> {
            while(true){
                content.repaint();
                try {
                    Thread.sleep(1L);
                } catch(final InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public static void main(final String[] args){
        new Canvas().setVisible(true);
    }
}
