package map.Noise.Color;

import map.Noise.PaintInterface;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.Serializable;

public class GradientNode implements GradientInterface, Comparable<GradientNode>, Cloneable, Serializable {
    private Color color;
    /*/
    Position of color node valued between 0 and 1;
     */
    private float position;

    private float[] hsb;
    private PaintInterface pi;

    private static final int BOX_SIZE = 20; // in pixel
    public static final int COLOR_HSV_ARRAY_SIZE = 4;

    public GradientNode(Color color, float position, PaintInterface pi) {
        this.pi = pi;
        hsb = new float[COLOR_HSV_ARRAY_SIZE];

        setPosition(position);
        setColor(color);
    }

    public float[] getHsb() {
        return hsb;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        hsb[3] = color.getAlpha();
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

    @Override
    public void brighter() {
        setColor(color.brighter());
    }

    @Override
    public void darker() {
        setColor(color.darker());
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
        return Float.compare(position, o.position);
    }

    @Override
    protected GradientNode clone() {
        GradientNode node;
        try{
            node =  (GradientNode) super.clone();
            node.hsb = new float[COLOR_HSV_ARRAY_SIZE];
            node.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
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
        return "R=" + color.getRed()+ ",G=" + color.getGreen() + ",B=" + color.getBlue() + ",A=" + color.getAlpha() + ",P=" + position;
    }

    public static GradientNode fromString(String str, PaintInterface pi)
    {
        String[] parts = str
                .replace("R=", "")
                .replace("G=", "")
                .replace("B=", "")
                .replace("A=", "")
                .replace("P=", "")
                .split(",");
        Color color;
        if(parts.length == 4)
        {
            color = new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            return new GradientNode(color, Float.parseFloat(parts[3]), pi);
        }
        else if(parts.length == 5)
        {
            color = new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            return new GradientNode(color, Float.parseFloat(parts[4]), pi);
        }
        else
            throw new RuntimeException("Error parsing color");

    }
}
