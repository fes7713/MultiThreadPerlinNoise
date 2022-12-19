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

        NoiseChunkGroup ncg = new NoiseChunkGroup("Chunk", fn, 5, 5);
        NoiseChunkGroup newNcg = new NoiseChunkGroup("New", fn, 5, 6);
        ncg.setDimension(500, 500);
        newNcg.setDimension(500, 600);
//        newNcg.setChunkX(2);

        JPanel panel = new NoiseMapPanel(ncg) ;
        newNcg.updateChunk(
                null
        );
        ncg.updateChunk(
                null
        );

        System.out.println(ncg);

        ncg.pushTop(newNcg, 2);
        System.out.println("pushed");

        System.out.println(ncg);

        panel.repaint();


        frame.add(panel);
        frame.setBounds(0, 0, 500, 500);
        frame.setVisible(true);
        frame.repaint();
    }

}
