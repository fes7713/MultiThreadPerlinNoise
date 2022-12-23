package Noise.Color;

import java.awt.*;

public class GradientNode implements Comparable<GradientNode> {
    Color color;
    /*/
    Position of color node valued between 0 and 1;
     */
    float position;

    private static int BOX_SIZE = 20; // in pixel

    float[] hsb;

    public GradientNode(Color color, float position) {
        this.color = color;
        if(position < 0 || position > 1)
            throw new IllegalArgumentException("position should be value between 0 and 1");
        this.position = position;

        hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
    }

    public float[] getHsb() {
        return hsb;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        this.color = color;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    @Override
    public int compareTo(GradientNode o) {
        if (position > o.position)
            return 1;
        else if (position == o.position)
            return 0;
        else
            return -1;
    }

    @Override
    public String toString()
    {
        return "[R:" + color.getRed()+ ",G:" + color.getGreen() + ",B:" + color.getBlue() + "]" + "pos:" + position;
    }

    public void paint(Graphics2D g2d, int width, int height)
    {
        g2d.setColor(color);

        if(width > height)
        {
            g2d.fillRect((int)(width * position), height - BOX_SIZE, BOX_SIZE, BOX_SIZE);
            g2d.setColor(Color.WHITE);
            g2d.drawRect((int)(width * position), height - BOX_SIZE, BOX_SIZE, BOX_SIZE);
        }

        else {
            g2d.fillRect(width - BOX_SIZE, (int) (height * position), BOX_SIZE, BOX_SIZE);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(width - BOX_SIZE, (int) (height * position), BOX_SIZE, BOX_SIZE);
        }
    }
}
