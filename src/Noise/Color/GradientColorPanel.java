package Noise.Color;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class GradientColorPanel extends JPanel {
    final List<GradientNode> nodes;
    BufferedImage bi;

    public GradientColorPanel(List<GradientNode> nodes)
    {
        this.nodes = nodes;
        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = this.getWidth();
        int height = this.getHeight();
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Collections.sort(nodes);

        if(width > height)
        {
            float min = nodes.get(nodes.size() - 1).getPosition();
            float max = nodes.get(0).getPosition();
            float length = max - min;

            Color[] colors= new Color[width];
            int cnt = 0;
            for(int i = 1; i < nodes.size(); i++)
            {
                Color preColor = nodes.get(i - 1).getColor();
                float[] prehsbvals = new float[3];
                Color.RGBtoHSB(preColor.getRed(), preColor.getGreen(), preColor.getBlue(), prehsbvals);

                Color color = nodes.get(i).getColor();
                float[] hsbvals = new float[3];
                Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbvals);
                for (int j = 0; cnt / (float)width < nodes.get(i).getPosition() && cnt < width; j++) {
                    float interval = nodes.get(i).getPosition() - nodes.get(i - 1).getPosition();
                    float ratio = (cnt / (float)width - nodes.get(i - 1).getPosition()) / interval;

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

                    colors[cnt] = Color.getHSBColor(newhsvvals[0], newhsvvals[1], newhsvvals[2]);

                    cnt++;
                }
            }


            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {


                    bi.setRGB(i, j, colors[i].getRGB());

                }
            }
        }
        else{
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bi.setRGB(i, j, getIntFromColor(i / (float)width, i / (float)width, i / (float)width));
                }
            }
        }
        Graphics2D g2d = (Graphics2D)g;

        g2d.drawImage(bi, 0, 0, width, height, null);
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
