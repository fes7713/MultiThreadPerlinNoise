package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GradientColorEditorPanel extends JPanel implements ComponentListener {
    private final JPanel buttonPanel;
    private final GradientColorPanel colorPanel;
    private final List<JButton> buttons;

    public GradientColorEditorPanel(PaintInterface pi)
    {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));

        buttons = new ArrayList<>();
        colorPanel = new GradientColorPanel(pi);

        Stream.of(ColorEditorAction.values()).forEach(action -> {
            JButton actionButton = new JButton(action.name());
            actionButton.setActionCommand(action.name());
            actionButton.addActionListener(colorPanel);
            buttonPanel.add(actionButton);
            buttons.add(actionButton);
        });

        add(colorPanel, BorderLayout.CENTER);
        addComponentListener(this);
        setLayout(new BorderLayout());
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

    public int[] getColors()
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

    public int[] getUpdatedColorArray(int size)
    {
        return colorPanel.getUpdatedColorArray(size);
    }

    public static void main(String[] argv)
    {
        GradientColorEditorPanel panel = new GradientColorEditorPanel(null);
        panel.showFrame();
    }
}
