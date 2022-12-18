import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

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

        NoiseChunkGroup ncg = new NoiseChunkGroup(5, 5);
        ncg.setChunkY(-1);
        JPanel panel = new NoiseMapPanel(ncg) ;

        ncg.updateChunk(
                panel::repaint,
                ncg::setNoiseRange
        );
        frame.add(panel);
        frame.setBounds(0, 0, 500, 500);
        frame.setVisible(true);
        frame.repaint();
    }

}
