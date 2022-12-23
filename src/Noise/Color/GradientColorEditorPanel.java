package Noise.Color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradientColorEditorPanel extends JPanel implements ComponentListener {
    JButton addButton;
    JButton removeButton;
    JPanel buttonPanel;

    JPanel colorPanel;
    public GradientColorEditorPanel()
    {
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        buttonPanel = new JPanel(new BorderLayout());

        addComponentListener(this);
        setLayout(new BorderLayout());

        colorPanel = new GradientColorPanel();
        add(colorPanel, BorderLayout.CENTER);
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

    public static void main(String[] argv)
    {
        GradientColorEditorPanel panel = new GradientColorEditorPanel();
        panel.showFrame();
    }
}