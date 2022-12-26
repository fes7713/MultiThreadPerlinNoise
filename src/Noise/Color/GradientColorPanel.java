package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GradientColorPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private BufferedImage bi;
    private PaintInterface pi;
    private boolean hold;
    private final List<GradientNodeLine> gradientLines;

    public GradientColorPanel(PaintInterface pi)
    {
        this.pi = pi;
        hold = false;

        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        addMouseListener(this);
        addMouseMotionListener(this);

        gradientLines = new ArrayList<>();
        gradientLines.add(new GradientNodeLine(0.2F, this::repaint));
    }

    public void setPaintInterface(PaintInterface pi)
    {
        System.err.println("Color panel paint interface has been updated");
        this.pi = pi;
    }

    //    public void set
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = this.getWidth();
        int height = this.getHeight();
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = (Graphics2D)bi.getGraphics();
        for(GradientNodeLine line: gradientLines)
            line.paint(g2d, width, height);
        g.drawImage(bi, 0, 0, width, height, null);
    }

    public int[] getUpdatedColorArray(int size)
    {
        // TODO delete this later
        // TODO fix get 0
        gradientLines.get(0).updateColorArray(size);
        return getColorArray();
    }
    public int[] getColorArray(){
        // TODO delete this later
        // TODO fix get 0
        return gradientLines.get(0).getColorArray();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for(GradientNodeLine line: gradientLines)
            line.mouseClicked(e, this.getWidth(), this.getHeight());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(GradientNodeLine line: gradientLines)
            line.mousePressed(e, this.getWidth(), this.getHeight());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        for(GradientNodeLine line: gradientLines)
            line.mouseDragged(e, this.getWidth(), this.getHeight());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(GradientNodeLine line: gradientLines)
            line.action(e, this);

    }
}
