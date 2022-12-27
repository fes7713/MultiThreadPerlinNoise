package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;

public class GradientNode implements GradientInterface, Comparable<GradientNode>, Cloneable{
    private Color color;
    /*/
    Position of color node valued between 0 and 1;
     */
    private float position;

    private static final int BOX_SIZE = 20; // in pixel

    private float[] hsb;
    private PaintInterface pi;

    public GradientNode(Color color, float position, PaintInterface pi) {
        this.color = color;
        this.pi = pi;
        hsb = new float[3];

        setPosition(position);
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
        this.position = Math.max(0, Math.min(1, position));
    }

    public void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
    }
    public boolean contains(MouseEvent event, float centerX, float height, boolean selected)
    {
        int multiplier = 1;
        if(selected)
            multiplier = 2;

        int x = event.getX();
        int y = event.getY();

        float length = BOX_SIZE * multiplier;

        if((height * position) - length / 2 < y && y < (height * position) + length / 2)
        {
            if(centerX - length / 2 < x && x < centerX + length / 2)
            {
                return true;
            }
        }
        return false;
    }

    public boolean mouseEvent(MouseEvent event, float centerX, int height, boolean selected)
    {
       if(contains(event, centerX, height, selected))
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
        dialog.setBounds(x, y, 600, 400);
        dialog.setVisible(true);
    }

    public void paint(Graphics2D g2d, int centerX, int height)
    {
        paint(g2d, centerX, height, false);
    }

    public void paint(Graphics2D g2d, int centerX, int height, boolean selected)
    {
        int multiplier = 1;
        if(selected)
            multiplier = 2;

        int length = BOX_SIZE * multiplier;

        g2d.setColor(color);
        g2d.fillRect(
                centerX - length / 2,
                (int) (height * position) - length / 2,
                length,
                length);

        if(hsb[2] * (1.25 - hsb[1]) > 1)
            g2d.setColor(color.darker().darker());
        else
            g2d.setColor(Color.WHITE);
        g2d.drawRect(
                centerX - length / 2,
                (int) (height * position) - length / 2,
                length,
                length);
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
    protected GradientNode clone() {
        GradientNode node;
        try{
            node =  (GradientNode) super.clone();
            node.hsb = new float[3];
            node.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
        }catch(CloneNotSupportedException e)
        {
            e.printStackTrace();
            return null;
        }
        return node;
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
}
