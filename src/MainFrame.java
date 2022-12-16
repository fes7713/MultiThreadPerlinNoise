import javax.swing.*;
import java.awt.*;
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
        frame.setBounds(0, 0, 500, 500);
        frame.setVisible(true);

        PerlinNoiseArray pna = new PerlinNoiseArray(fn, 500, 500);


        JPanel panel = new JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                pna.updateNoiseMap();
                BufferedImage bi = pna.getImage();
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(bi, 0 ,0, null);
            }
        };

        frame.add(panel);
        frame.repaint();
    }

}
