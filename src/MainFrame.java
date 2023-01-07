import Noise.FastNoise;

import javax.swing.*;

public class MainFrame {
    public static void main(String[] argv)
    {
        FastNoise fn = new FastNoise();

        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);
        double noise = fn.GetNoise(0, 0);
        System.out.println(noise);

        JFrame frame = new JFrame("New Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        NoiseMapPanel panel = new NoiseMapPanel() ;
        //            panel.updateImage();
        MapEditor mapEditor = new MapEditor(panel, panel::repaint);
        mapEditor.showMapEditor();
        panel.showColorEditor();
        panel.showVariableChanger();
        panel.showLightingChanger();
        panel.repaint();


        frame.add(panel);
        frame.setBounds(0, 0, 500, 500);
        frame.setVisible(true);
        frame.repaint();

    }

}
