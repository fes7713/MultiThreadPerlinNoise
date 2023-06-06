package map;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class MainFrame {
    public static void main(String[] argv)
    {
        JFrame frame = new JFrame("New Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        NoiseMapPanel panel = new NoiseMapPanel(2, 2) ;
        OverlayLayout layout = new OverlayLayout(panel);
        panel.setLayout(layout);
        panel.showLightingChanger();
        panel.showVariableChanger();
        panel.showColorEditor();

//        NoiseMapPanel cloud = new NoiseMapPanel(2, 2) ;
//        NoiseMapPanel cloudHigh = new NoiseMapPanel(2, 2) ;
//        cloud.setResolutionMin(-4);
//        cloud.setResolutionMax(1);
//        cloud.loadColorPreset("cloud3.txt");
//        cloud.loadVariables("cloud2.txt");
//        cloud.clearChunks();
//        cloud.updateChunkGroups();
//        cloud.setOpaque(false);
//
//        cloudHigh.setResolutionMin(-6);
//        cloudHigh.setResolutionMax(-4);
//        cloudHigh.loadColorPreset("cloud3.txt");
//        cloudHigh.loadVariables("cloudHigh1.txt");
//        cloudHigh.clearChunks();
//        cloudHigh.updateChunkGroups();
//        cloudHigh.setOpaque(false);
//
//        panel.add(cloud);
//        panel.add(cloudHigh);
////
////        cloud.addComponentListener(panel);
////        cloud.addMouseMotionListener(panel);
////        cloud.addMouseListener(panel);
////        cloud.addMouseWheelListener(panel);
////
////        cloud.addComponentListener(cloudHigh);
////        cloud.addMouseMotionListener(cloudHigh);
////        cloud.addMouseListener(cloudHigh);
////        cloud.addMouseWheelListener(cloudHigh);
//
//
////        panel.add(cloud);
//
//        cloudHigh.showColorEditor();
//        cloudHigh.showMapEditor();
//        cloudHigh.showVariableChanger();
//
////        panel.showColorEditor();
////        panel.showVariableChanger();
////        panel.showLightingChanger();
////        panel.showMapEditor();
////        panel.repaint();
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                cloud.setCenterX(cloud.getCenterX() + 2);
//                cloud.setStartLeft(cloud.getStartLeft() - 2);
////                cloud.moveLeft(10);
//
//                cloudHigh.setCenterX(cloudHigh.getCenterX() + 0.6F);
//                cloudHigh.setStartLeft(cloudHigh.getStartLeft() - 0.6F);
//                cloudHigh.setCenterY(cloudHigh.getCenterY() + 0.6F);
//                cloudHigh.setStartTop(cloudHigh.getStartTop() - 0.6F);
//
////                cloudHigh.repaint();
//                cloud.repaint();
//            }
//        }, 1000, 50);
        frame.add(panel);
        frame.setBounds(0, 0, 500, 500);
        frame.setVisible(true);
//        frame.repaint();

    }

}
