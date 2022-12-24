package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class GradientColorEditorPanel extends JPanel implements ComponentListener {
    JButton addButton;
    JButton removeButton;
    JPanel buttonPanel;

    GradientColorPanel colorPanel;

    public GradientColorEditorPanel(PaintInterface pi)
    {
        addButton = new JButton(ColorEditorAction.ADD.name());
        removeButton = new JButton(ColorEditorAction.REMOVE.name());
        buttonPanel = new JPanel(new BorderLayout());

        addComponentListener(this);
        setLayout(new BorderLayout());

        colorPanel = new GradientColorPanel(pi);
        add(colorPanel, BorderLayout.CENTER);
        addButton.addActionListener((ActionListener) colorPanel);
        addButton.setActionCommand(ColorEditorAction.ADD.name());
        removeButton.addActionListener(colorPanel);
        removeButton.setActionCommand(ColorEditorAction.REMOVE.name());
    }

    public void setPaintInterface(PaintInterface pi)
    {
        colorPanel.setPaintInterface(pi);
    }

    public void showFrame()
    {
        JFrame frame = new JFrame("Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 600);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(150, 200));
        frame.add(this);
        frame.setVisible(true);
    }

    public Color[] getColors()
    {
        return colorPanel.getColorArray();
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

    public Color[] getUpdatedColorArray(int size)
    {
        return colorPanel.getUpdatedColorArray(size);
    }

    public static void main(String[] argv)
    {
        GradientColorEditorPanel panel = new GradientColorEditorPanel(null);
        panel.showFrame();
    }
}
