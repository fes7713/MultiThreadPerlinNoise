package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GradientColorPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    final List<GradientNode> nodes;
    BufferedImage bi;
    Color[] colors;
    GradientNode selectedNode;
    PaintInterface pi;
    boolean hold;


    public GradientColorPanel(PaintInterface pi)
    {
        this.pi = pi;
        hold = false;
        nodes = new ArrayList<>();
        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        addMouseListener(this);
        addMouseMotionListener(this);
        nodes.add(new GradientNode(Color.RED, 0.1F, this::repaint));
        nodes.add(new GradientNode(Color.MAGENTA, 0.25F, this::repaint));
        nodes.add(new GradientNode(Color.ORANGE, 0.5F, this::repaint));
        nodes.add(new GradientNode(Color.WHITE, 0.9F, this::repaint));
        selectedNode = nodes.get(0);
        updateColorArray(255);
    }

//    public void set
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = this.getWidth();
        int height = this.getHeight();
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        if(width > height)
        {
            updateColorArray(width);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bi.setRGB(i, j, colors[i].getRGB());
                }
            }
        }
        else{
            updateColorArray(height);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bi.setRGB(i, j, colors[j].getRGB());
                }
            }
        }
        Graphics2D g2d = (Graphics2D)g;

        g2d.drawImage(bi, 0, 0, width, height, null);
        selectedNode.paint(g2d, width, height, true);
        for(GradientNode node: nodes)
            if(node != selectedNode)
                node.paint(g2d, width, height);
    }

    public Color[] getUpdatedColorArray(int size)
    {
        updateColorArray(size);
        return colors;
    }

    public Color[] getColorArray(){
        return colors;
    }

    private void updateColorArray(int size)
    {
        if(nodes.size() < 2)
            throw new RuntimeException("Node list cannot be less than 2");
        Collections.sort(nodes);
        List<GradientNode> gradationNodes = new ArrayList<>();
        for(GradientNode node: nodes)
            gradationNodes.add(node);

        if(gradationNodes.get(0).getPosition() != 0)
            gradationNodes.add(0, new GradientNode(gradationNodes.get(0).getColor(), 0, this::repaint));

        if(gradationNodes.get(gradationNodes.size() - 1).getPosition() != 1)
            gradationNodes.add(new GradientNode(gradationNodes.get(gradationNodes.size() - 1).getColor(), 1, this::repaint));

        colors= new Color[size];
        int cnt = 0;

        for(int i = 1; i < gradationNodes.size(); i++)
        {
            float[] prehsbvals = gradationNodes.get(i - 1).getHsb();

            float[] hsbvals = gradationNodes.get(i).getHsb();

            for (int j = 0; cnt / (float)size < gradationNodes.get(i).getPosition() && cnt < size; j++) {
                float interval = gradationNodes.get(i).getPosition() - gradationNodes.get(i - 1).getPosition();
                float ratio = (cnt / (float)size - gradationNodes.get(i - 1).getPosition()) / interval;

                if(Math.abs(prehsbvals[0] - hsbvals[0]) > 0.5)
                {
                    if(prehsbvals[0] > hsbvals[0])
                        hsbvals[0] += 1;
                    else
                        prehsbvals[0] += 1;
                }

                float[] newhsvvals = new float[3];
                for (int k = 0; k < 3; k++) {
                    newhsvvals[k] = prehsbvals[k] * (1 - ratio) + hsbvals[k] * ratio;
                }

                if(newhsvvals[0] > 1)
                    newhsvvals[0] -= 1;

                colors[cnt++] = Color.getHSBColor(newhsvvals[0], newhsvvals[1], newhsvvals[2]);
            }
        }

        if(pi != null)
            pi.paint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        for(GradientNode node: nodes)
        {
            if(node.mouseEvent(e, this.getWidth(), this.getHeight(), node == selectedNode))
            {
                selectedNode = node;
                return;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        for(GradientNode node: nodes)
        {
            if(node == selectedNode)
                continue;
            if(node.contains(e, this.getWidth(), this.getHeight(), false))
            {
                selectedNode = node;
                hold = true;
                return;
            }
        }
        if(selectedNode.contains(e, this.getWidth(), this.getHeight(), true))
        {
            hold = true;
            return;
        }
        hold = false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(hold)
        {
            int x = e.getX();
            int y = e.getY();

            int width = getWidth();
            int height = getHeight();

            if(width > height) {
                selectedNode.setPosition(x / (float)width);
            }
            else{
                selectedNode.setPosition(y / (float)height);
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());

        switch (ColorEditorAction.valueOf(e.getActionCommand()))
        {
            case ADD -> {
                GradientNode newNode;
                if(selectedNode.getPosition() < 0.9F)
                    newNode = new GradientNode(selectedNode, selectedNode.getPosition() + 0.05F);
                else
                    newNode =new GradientNode(selectedNode, selectedNode.getPosition() - 0.05F);

                nodes.add(newNode);
                selectedNode = newNode;
            }
            case REMOVE -> {
                if(nodes.size() <= 2)
                    return;
                int index = nodes.indexOf(selectedNode);
                nodes.remove(selectedNode);
                if(index != 0)
                    selectedNode = nodes.get(index - 1);
                else
                    selectedNode = nodes.get(0);
            }
            default -> {
                throw new RuntimeException("Error occurred in color gradient editor");
            }
        }
        repaint();
    }
}
