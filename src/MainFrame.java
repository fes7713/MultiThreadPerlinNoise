import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class MainFrame {
    public static void main(String[] argv)
    {
        JFrame frame = new JFrame("New Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        NoiseMapPanel panel = new NoiseMapPanel() ;
//        NoiseMapPanel cloud = new NoiseMapPanel() ;
//        cloud.setResolutionMin(-2);
//        cloud.setResolutionMax(5);
//        cloud.loadColorPreset("cloud.txt");
//        cloud.clearChunks();
//        cloud.updateChunkGroups();
//        cloud.setOpaque(false);


        OverlayLayout layout = new OverlayLayout(panel);
        panel.setLayout(layout);
//        panel.add(cloud);
//
//        cloud.addComponentListener(panel);
//        cloud.addMouseMotionListener(panel);
//        cloud.addMouseListener(panel);
//        cloud.addMouseWheelListener(panel);
        //            panel.updateImage();
//        MapEditor mapEditor = new MapEditor(panel, panel::repaint);
//        mapEditor.showMapEditor();
//        panel.add(cloud);


//        cloud.showColorEditor();
//        panel.showColorEditor();
//        panel.showVariableChanger();
//        panel.showLightingChanger();
//        panel.showMapEditor();
//        panel.repaint();

//        frame.addMouseWheelListener(panel);
//        frame.addMouseListener(panel);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                panel.setCenterX(panel.getCenterX() + 1);
                panel.setStartLeft(panel.getStartLeft() - 1);
                panel.repaint();
            }
        }, 1, 10);
        frame.add(panel);
        frame.setBounds(0, 0, 500, 500);
        frame.setVisible(true);
//        frame.repaint();

    }

}
