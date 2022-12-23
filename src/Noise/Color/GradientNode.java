package Noise.Color;

import java.awt.*;

public class GradientNode implements Comparable<GradientNode> {
    Color color;
    /*/
    Position of color node valued between 0 and 1;
     */
    float position;

    public GradientNode(Color color, float position) {
        this.color = color;
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
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
}
