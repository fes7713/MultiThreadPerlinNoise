package Noise.Color;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GradientColorPanel extends JPanel implements ComponentListener {
    List<GradientNode> nodes;
    JButton addButton;
    JButton removeButton;
    JPanel buttonPanel;

    JPanel colorPanel;

    BufferedImage bi;
    public GradientColorPanel()
    {
        nodes = new ArrayList<>();

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        buttonPanel = new JPanel(new BorderLayout());
//        add(removeButton);
        addComponentListener(this);
        setLayout(new BorderLayout());
        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        colorPanel = new JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = this.getWidth();
                int height = this.getHeight();
                bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                if(width > height)
                {
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            bi.setRGB(i, j, getIntFromColor(i / (float)width, i / (float)width, i / (float)width));
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
        };
        add(colorPanel, BorderLayout.CENTER);
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

    public void showFrame()
    {
        JFrame frame = new JFrame("Window");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 600);
        frame.setLocationRelativeTo(null);

        frame.setMinimumSize(new Dimension(100, 250));

        frame.add(this);
        frame.setVisible(true);



    }

    @Override
    public void componentResized(ComponentEvent e) {
        buttonPanel.removeAll();
        buttonPanel.revalidate();

        removeAll();
        revalidate();
        repaint();

        int width = e.getComponent().getWidth();
        int height = e.getComponent().getHeight();

        if(width > height)
        {
            buttonPanel.add(addButton, BorderLayout.NORTH);
            buttonPanel.add(removeButton, BorderLayout.SOUTH);
            add(buttonPanel, BorderLayout.EAST);
        }
        else
        {
            buttonPanel.add(addButton, BorderLayout.WEST);
            buttonPanel.add(removeButton, BorderLayout.EAST);
            add(buttonPanel, BorderLayout.NORTH);
        }
        add(colorPanel, BorderLayout.CENTER);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    private class GradientNode implements Comparable<GradientNode> {
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
            if(position > o.position)
                return 1;
            else if(position == o.position)
                return 0;
            else
                return -1;
        }

//        @Override
//        public int compare(GradientNode o1, GradientNode o2) {
//            if(o1.position > o2.position)
//                return 1;
//            else if(o1.position == o2.position)
//                return 0;
//            else
//                return -1;
//        }

    }
    public static void main(String[] argv)
    {
        GradientColorPanel panel = new GradientColorPanel();
        panel.showFrame();
    }
}
