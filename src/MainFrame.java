import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class MainFrame {
    public static void main(String[] argv)
    {
        System.out.println("Print");
        FastNoise fn = new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);
        double noise = fn.GetNoise(0, 0);
        System.out.println(noise);

        JFrame frame = new JFrame("New Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


//        PerlinNoiseArray pna = new PerlinNoiseArray(fn, 50, 50, 500, 500);
        NoiseChunkManager ncm = new NoiseChunkManager(5, 5);

        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                ncm.updateChunk();
                ncm.drawImage(g2d);
            }
        };

        panel.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = e.getComponent().getWidth();
                int height = e.getComponent().getHeight();

                System.out.println(width);
                System.out.println(height);
                ncm.setDimension(width, height);
                panel.repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });


        frame.add(panel);
        frame.setBounds(0, 0, 500, 500);
        frame.setVisible(true);
        frame.repaint();
    }

}
