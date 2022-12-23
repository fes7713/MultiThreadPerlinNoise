package Noise.Color;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradientColorPanel extends JPanel {
    final List<GradientNode> nodes;
    BufferedImage bi;
    Color[] colors;

    public GradientColorPanel()
    {
        nodes = new ArrayList<>();
        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        nodes.add(new GradientNode(Color.RED, 0.1F));
        nodes.add(new GradientNode(Color.MAGENTA, 0.25F));
        nodes.add(new GradientNode(Color.ORANGE, 0.5F));
        nodes.add(new GradientNode(Color.WHITE, 0.9F));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = this.getWidth();
        int height = this.getHeight();
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);



        if(width > height)
        {
            updateColorArray(width);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bi.setRGB(i, j, colors[i].getRGB());
                }
            }
        }
        else{
            updateColorArray(height);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bi.setRGB(i, j, colors[j].getRGB());
                }
            }
        }
        Graphics2D g2d = (Graphics2D)g;

        g2d.drawImage(bi, 0, 0, width, height, null);
        for(GradientNode node: nodes)
            node.paint(g2d, width, height);
    }

    public void addColorAtPosition(Color color, float position)
    {
        if(position < 0 || position > 1)
            throw new IllegalArgumentException("position should be value between 0 and 1");

        nodes.add(new GradientNode(color, position));
        if(nodes.size() < 2)
            System.err.println("Node list size is still less than 2");
    }

    private void updateColorArray(int size)
    {
        if(nodes.size() < 2)
            throw new RuntimeException("Node list cannot be less than 2");
        Collections.sort(nodes);
        List<GradientNode> gradationNodes = new ArrayList<>(nodes);
        if(gradationNodes.get(0).getPosition() != 0)
            gradationNodes.add(0, new GradientNode(gradationNodes.get(0).getColor(), 0));

        if(gradationNodes.get(gradationNodes.size() - 1).getPosition() != 1)
            gradationNodes.add(new GradientNode(gradationNodes.get(gradationNodes.size() - 1).getColor(), 1));

        colors= new Color[size];
        int cnt = 0;

        for(int i = 1; i < gradationNodes.size(); i++)
        {
            float[] prehsbvals = gradationNodes.get(i - 1).getHsb();

            float[] hsbvals = gradationNodes.get(i).getHsb();

            for (int j = 0; cnt / (float)size < gradationNodes.get(i).getPosition() && cnt < size; j++) {
                float interval = gradationNodes.get(i).getPosition() - gradationNodes.get(i - 1).getPosition();
                float ratio = (cnt / (float)size - gradationNodes.get(i - 1).getPosition()) / interval;

                if(Math.abs(prehsbvals[0] - hsbvals[0]) > 0.5)
                {
                    if(prehsbvals[0] > hsbvals[0])
                        hsbvals[0] += 1;
                    else
                        prehsbvals[0] += 1;
                }

                float[] newhsvvals = new float[3];
                for (int k = 0; k < 3; k++) {
                    newhsvvals[k] = prehsbvals[k] * (1 - ratio) + hsbvals[k] * ratio;
                }

                if(newhsvvals[0] > 1)
                    newhsvvals[0] -= 1;

                colors[cnt++] = Color.getHSBColor(newhsvvals[0], newhsvvals[1], newhsvvals[2]);
            }
        }
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public int getIntFromColor(float Red, float Green, float Blue){
        int R = Math.round(255 * Red);
        int G = Math.round(255 * Green);
        int B = Math.round(255 * Blue);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return 0xFF000000 | R | G | B;
    }
}
