package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class GradientNode implements Comparable<GradientNode> {
    Color color;
    /*/
    Position of color node valued between 0 and 1;
     */
    float position;

    private static int BOX_SIZE = 20; // in pixel

    float[] hsb;
    PaintInterface pi;

    public GradientNode(GradientNode node, float position) {
        this(node.color, position, node.pi);
    }

    public GradientNode(Color color, float position, PaintInterface pi) {
        this.color = color;
        if(position < 0 || position > 1)
            throw new IllegalArgumentException("position should be value between 0 and 1");
        this.position = position;
        this.pi = pi;

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
        return "R=" + color.getRed()+ ",G=" + color.getGreen() + ",B=" + color.getBlue() + ",P=" + position;
    }

    public static GradientNode fromString(String str, PaintInterface pi)
    {
        String[] parts = str
                .replace("R=", "")
                .replace("G=", "")
                .replace("B=", "")
                .replace("P=", "")
                .split(",");

        Color color = new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        return new GradientNode(color, Float.parseFloat(parts[3]), pi);
    }

    public boolean contains(MouseEvent event, int width, int height, boolean selected)
    {
        int multiplier = 1;
        if(selected)
            multiplier = 2;
        int x = event.getX();
        int y = event.getY();

        if(width > height) {
            // x coord check
            if ((int) (width * position) - BOX_SIZE * multiplier / 2 < x && x < (int) (width * position) + BOX_SIZE * multiplier / 2) {
                // y coord check
                if(y > height - BOX_SIZE * multiplier)
                {
                    return true;
                }
            }
        }
        else{
            if((int) (height * position) - BOX_SIZE * multiplier / 2 < y && y < (int) (height * position) + BOX_SIZE * multiplier / 2)
            {
                if(width - BOX_SIZE * multiplier < x)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean mouseEvent(MouseEvent event, int width, int height, boolean selected)
    {
       if(contains(event, width, height, selected))
       {
           showColorPalette(event.getXOnScreen() + 20, event.getYOnScreen() - 20);
           return true;
       }
       return false;
    }

    private void showColorPalette(int x, int y)
    {
        final JColorChooser colorChooser = new JColorChooser(color);
        Color original = color;
        JDialog dialog = JColorChooser.createDialog(null, "Title", false, colorChooser,
                e -> {
                    System.out.println("Okay ");
                    Color newForegroundColor = colorChooser.getColor();
                    setColor(newForegroundColor);
                    pi.paint();
                },
                e -> {
                    System.out.println("Cancel");
                    setColor(original);
                    pi.paint();
                }
        );

        ColorSelectionModel model = colorChooser.getSelectionModel();
        ChangeListener changeListener = changeEvent -> {
            Color newForegroundColor = colorChooser.getColor();
            setColor(newForegroundColor);
            pi.paint();
        };
        model.addChangeListener(changeListener);
//        model.setSelectedColor(color);
        dialog.setBounds(x, y, 600, 400);
        dialog.setVisible(true);
    }

    public void paint(Graphics2D g2d, int width, int height)
    {
        paint(g2d, width, height, false);
    }

    public void paint(Graphics2D g2d, int width, int height, boolean selected)
    {
        g2d.setColor(color);

        int multiplier = 1;
        if(selected)
            multiplier = 2;

        if(width > height)
        {
            g2d.fillRect(
                    (int)(width * position) - BOX_SIZE * multiplier / 2,
                    height - BOX_SIZE * multiplier,
                    BOX_SIZE * multiplier,
                    BOX_SIZE * multiplier);
            if(hsb[2] * (1.25 - hsb[1]) > 1)
                g2d.setColor(color.darker().darker());
            else
                g2d.setColor(Color.WHITE);
            g2d.drawLine((int)(width * position), 0, (int)(width * position), height - BOX_SIZE * multiplier);
            g2d.drawRect(
                    (int)(width * position) - BOX_SIZE * multiplier / 2,
                    height - BOX_SIZE * multiplier,
                    BOX_SIZE * multiplier,
                    BOX_SIZE * multiplier);
        }

        else {
            g2d.fillRect(
                    width - BOX_SIZE * multiplier,
                    (int) (height * position) - BOX_SIZE * multiplier / 2,
                    BOX_SIZE * multiplier,
                    BOX_SIZE * multiplier);
            if(hsb[2] * (1.25 - hsb[1]) > 1)
                g2d.setColor(color.darker().darker());
            else
                g2d.setColor(Color.WHITE);
            g2d.drawLine(0, (int) (height * position), width - BOX_SIZE * multiplier, (int) (height * position));
            g2d.drawRect(
                    width - BOX_SIZE * multiplier,
                    (int) (height * position) - BOX_SIZE * multiplier / 2,
                    BOX_SIZE * multiplier,
                    BOX_SIZE * multiplier);
        }
    }
}
