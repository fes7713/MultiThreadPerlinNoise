package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GradientColorEditorPanel extends JPanel implements ComponentListener {
    JButton addButton;
    JButton removeButton;
    JButton loadButton;
    JButton saveButton;

    JPanel buttonPanel;


    GradientColorPanel colorPanel;
    List<JButton> buttons;

    public GradientColorEditorPanel(PaintInterface pi)
    {
        addButton = new JButton(ColorEditorAction.ADD.name());
        removeButton = new JButton(ColorEditorAction.REMOVE.name());
        loadButton = new JButton(ColorEditorAction.LOAD.name());
        saveButton = new JButton(ColorEditorAction.SAVE.name());

        buttons = Stream.of(addButton, removeButton, loadButton, saveButton).toList();

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        buttons.forEach(buttonPanel::add);
        addComponentListener(this);
        setLayout(new BorderLayout());

        colorPanel = new GradientColorPanel(pi);
        add(colorPanel, BorderLayout.CENTER);
        addButton.setActionCommand(ColorEditorAction.ADD.name());
        removeButton.setActionCommand(ColorEditorAction.REMOVE.name());
        loadButton.setActionCommand(ColorEditorAction.LOAD.name());
        saveButton.setActionCommand(ColorEditorAction.SAVE.name());
        addButton.addActionListener(colorPanel);
        removeButton.addActionListener(colorPanel);
        loadButton.addActionListener(colorPanel);
        saveButton.addActionListener(colorPanel);

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
            buttonPanel.setLayout(new GridLayout(4, 1));
            add(buttonPanel, BorderLayout.EAST);
        }
        else
        {
            buttonPanel.setLayout(new GridLayout(2, 2));
            add(buttonPanel, BorderLayout.NORTH);
        }

        buttons.forEach(buttonPanel::add);
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
