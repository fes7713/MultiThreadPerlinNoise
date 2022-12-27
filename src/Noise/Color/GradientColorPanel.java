package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradientColorPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private BufferedImage bi;
    private PaintInterface pi;
    private boolean hold;
    private final List<GradientNodeLine> lines;
    private GradientNodeLine selectedLine;
    private int[][] array2D;

    public GradientColorPanel(PaintInterface pi)
    {
        this.pi = pi;
        hold = false;

        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        addMouseListener(this);
        addMouseMotionListener(this);

        lines = new ArrayList<>();
        lines.add(new GradientNodeLine(0.2F, this::repaint));
        lines.add(new GradientNodeLine(0.4F, this::repaint));

        lines.add(new GradientNodeLine(0.6F, this::repaint));
        selectedLine = lines.get(0);
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

        updateColorArray(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bi.setRGB(i, j, array2D[i][j]);
            }
        }

        Graphics2D g2d = (Graphics2D)bi.getGraphics();
        for(GradientNodeLine line: lines)
            line.paint(g2d, width, height, line==selectedLine);
        g.drawImage(bi, 0, 0, width, height, null);
    }

    public int[][] getUpdatedColor2DArray(int width, int height)
    {
        updateColorArray(width, height);
        return array2D;
    }

    private void updateColorArray(int width, int height){
        if(lines.size() < 1)
            throw new RuntimeException("Line list cannot be less than 1");
        Collections.sort(lines);

        Color[][] tempArray2D = new Color[lines.size()][height];
        for(int i = 0; i < lines.size(); i++)
        {
            lines.get(i).updateColorArray(height, false);
            tempArray2D[i] = lines.get(i).getColorArray();
        }

        array2D = new int[width][height];

        if(lines.get(0).getLinePosition() > 0)
        {
            for (int i = 0; i < height; i++)
            {
                int color = tempArray2D[0][i].getRGB();
                for (int j = 0; j / (float)width < lines.get(0).getLinePosition(); j++) {
                    array2D[j][i] = color;
                }
            }
        }

        int size = lines.size();
        for (int i = (int)(width * lines.get(size - 1).getLinePosition()); i < width; i++) {
            for (int j = 0; j < height; j++)
            {
                array2D[i][j] = tempArray2D[size - 1][j].getRGB();
            }
        }

        for (int p = 0; p < height; p++) {
            int cnt = (int)(lines.get(0).getLinePosition() * width);
            for(int i = 1; i < size; i++) {
                float[] prehsbvals = new float[3];
                Color.RGBtoHSB(tempArray2D[i - 1][p].getRed(), tempArray2D[i - 1][p].getGreen(), tempArray2D[i - 1][p].getBlue(), prehsbvals);

                float[] hsbvals = new float[3];
                Color.RGBtoHSB(tempArray2D[i][p].getRed(), tempArray2D[i][p].getGreen(), tempArray2D[i][p].getBlue(), hsbvals);

                for (int j = 0; cnt / (float) width < lines.get(i).getLinePosition() && cnt < width; j++) {
                    float interval = lines.get(i).getLinePosition() - lines.get(i - 1).getLinePosition();
                    float ratio = (cnt / (float) width - lines.get(i - 1).getLinePosition()) / interval;

                    if (Math.abs(prehsbvals[0] - hsbvals[0]) > 0.5) {
                        if (prehsbvals[0] > hsbvals[0])
                            hsbvals[0] += 1;
                        else
                            prehsbvals[0] += 1;
                    }

                    float[] newhsvvals = new float[3];
                    for (int k = 0; k < 3; k++) {
                        newhsvvals[k] = prehsbvals[k] * (1 - ratio) + hsbvals[k] * ratio;
                    }

                    if (newhsvvals[0] > 1)
                        newhsvvals[0] -= 1;

                    array2D[cnt++][p] = Color.getHSBColor(newhsvvals[0], newhsvvals[1], newhsvvals[2]).getRGB();
                }
            }
        }

        if(pi != null)
            repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int width = this.getWidth();
        int height = this.getHeight();

        selectedLine.mouseClicked(e, width, height);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int width = this.getWidth();
        int height = this.getHeight();

        selectedLine.mousePressed(e, width, height);

        for(GradientNodeLine line: lines)
        {
            if(line == selectedLine)
                continue;
            if(line.contains(e, width, height, false))
            {
                selectedLine = line;
                hold = true;
                repaint();
                return;
            }
        }
        if(selectedLine.contains(e, width, height, true))
        {
            hold = true;
            return;
        }
        hold = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int width = this.getWidth();
        int height = this.getHeight();

        if(!selectedLine.mouseDragged(e, width, height)) {
            if (hold) {
                int x = e.getX();
                int y = e.getY();

                selectedLine.setLinePosition(x / (float) getWidth());
                repaint();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(GradientNodeLine line: lines)
            line.action(e, this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
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
}
