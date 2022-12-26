package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class GradientNodeLine  implements Comparable<GradientNodeLine>{
    private float linePosition;
    private final List<GradientNode> nodes;
    private GradientNode selectedNode;
    private Color[] colors;

    boolean hold;

    PaintInterface pi;

    private static int LINE_THICKNESS = 10;

    public GradientNodeLine(List<GradientNode> nodes, float linePosition, PaintInterface pi) {
        this.nodes = nodes;

        /*/
        This is position of gradient line. Ratio to the available width;
        This field should be between 0 and 1;
         */
        this.linePosition = linePosition;
        nodes.add(new GradientNode(Color.WHITE, 0F, pi));
        nodes.add(new GradientNode(Color.BLACK, 1F, pi));
        selectedNode = nodes.get(0);

        // TODO This could cause paint issue
        updateColorArray(255, true);
        this.pi = pi;
        hold = false;
    }

    public GradientNodeLine(float linePosition, PaintInterface pi) {
        this(new ArrayList<>(), linePosition, pi);
    }
    public float getLinePosition()
    {
        return linePosition;
    }

    public void setLinePosition(float linePosition)
    {
        this.linePosition = Math.max(0, Math.min(1, linePosition));
    }

    public void setPaintInterface(PaintInterface pi)
    {
        System.err.println("Color panel paint interface has been updated");
        this.pi = pi;
    }

    public Color[] getUpdatedColorArray(int size)
    {
        updateColorArray(size, true);
        return colors;
    }

    public Color[] getColorArray(){
        return colors;
    }

    public void updateColorArray(int size, boolean update)
    {
        if(nodes.size() < 1)
            throw new RuntimeException("Node list cannot be less than 1");
        Collections.sort(nodes);

        colors= new Color[size];
        for (int i = 0; i < size * nodes.get(0).getNodePosition(); i++) {
            colors[i] = nodes.get(0).getColor();
        }

        for (int i = (int)(size * nodes.get(nodes.size() - 1).getNodePosition()); i < size; i++) {
            colors[i] = nodes.get(nodes.size() - 1).getColor();
        }

        int cnt = (int)(size * nodes.get(0).getNodePosition());

        for(int i = 1; i < nodes.size(); i++)
        {
            float[] prehsbvals = nodes.get(i - 1).getHsb();
            float[] hsbvals = nodes.get(i).getHsb();

            for (int j = 0; cnt / (float)size < nodes.get(i).getNodePosition() && cnt < size; j++) {
                float interval = nodes.get(i).getNodePosition() - nodes.get(i - 1).getNodePosition();
                float ratio = (cnt / (float)size - nodes.get(i - 1).getNodePosition()) / interval;

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

        if(pi != null && update)
            pi.paint();
    }

    public void paint(Graphics2D g2d, int width, int height, boolean selected)
    {
        int multiplier = 1;
        if(selected)
            multiplier = 2;

        int length = LINE_THICKNESS * multiplier;

        BufferedImage bi = new BufferedImage(length, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                bi.setRGB(i, j, colors[j].getRGB());
            }
        }
        g2d.drawImage(bi, (int)(linePosition * width) - length / 2, 0, length, height, null);
        selectedNode.paint(g2d, (int)(linePosition * width), height, true);
        for(GradientNode node: nodes)
            if(node != selectedNode)
                node.paint(g2d, (int)(linePosition * width), height);
    }

    public boolean contains(MouseEvent event, int width, int height, boolean selected)
    {
        int multiplier = 1;
        if(selected)
            multiplier = 2;

        int x = event.getX();

        float length = LINE_THICKNESS * multiplier;
        float position = linePosition * width;

        if(position - length / 2 < x && x < position +  length / 2)
        {
            return true;
        }

        for(GradientNode node: nodes)
        {
            if(node.contains(event, linePosition * width, height, node == selectedNode))
            {
                selectedNode = node;
                return true;
            }
        }

        return false;
    }

    public void mouseClicked(MouseEvent e, int width, int height)
    {
        if(e.getClickCount() == 2)
        {
            for(GradientNode node: nodes)
            {
                if(node.mouseEvent(e, linePosition * width, height, node == selectedNode))
                {
                    selectedNode = node;
                    return;
                }
            }
        }
    }

    public void mousePressed(MouseEvent e, int width, int height)
    {
        for(GradientNode node: nodes)
        {
            if(node == selectedNode)
                continue;
            if(node.contains(e, linePosition * width, height, false))
            {
                selectedNode = node;
                hold = true;
                pi.paint();
                return;

            }
        }
        if(selectedNode.contains(e, linePosition * width, height, true))
        {
            hold = true;
            return;
        }
        hold = false;
    }

    public boolean mouseDragged(MouseEvent e, int width, int height)
    {
        if(hold)
        {
            int x = e.getX();
            int y = e.getY();

            selectedNode.setNodePosition(y / (float)height);
            pi.paint();
            return true;
        }
        return false;
    }

    public void action(ActionEvent e, JPanel parent)
    {
        System.out.println(e.getActionCommand());

        switch (ColorEditorAction.valueOf(e.getActionCommand()))
        {
            case ADD_CELL -> {
                GradientNode newNode;
                if(selectedNode.getNodePosition() < 0.9F)
                    newNode = new GradientNode(selectedNode, selectedNode.getNodePosition() + 0.05F);
                else
                    newNode =new GradientNode(selectedNode, selectedNode.getNodePosition() - 0.05F);

                nodes.add(newNode);
                selectedNode = newNode;
                pi.paint();
            }
            case DEL_CELL -> {
                if(nodes.size() <= 2)
                    return;
                int index = nodes.indexOf(selectedNode);
                nodes.remove(selectedNode);
                if(index != 0)
                    selectedNode = nodes.get(index - 1);
                else
                    selectedNode = nodes.get(0);
                pi.paint();
            }
            case LOAD -> {
                try{
                    File[] files= new File("presets").listFiles();

                    String[] values = new String[files.length];
                    IntStream.range(0, values.length).forEach(i -> {
                        values[i] = files[i].getName();
                    });

                    Object value = JOptionPane.showInputDialog(parent, "Message",
                            "Load presets", JOptionPane.ERROR_MESSAGE,
                            new ImageIcon("icons/preset1.png"), values, values[0]);

                    if(value == null)
                        return;

                    List<GradientNode> inputNodes = new ArrayList<>();
                    BufferedReader inputReader = new BufferedReader(new FileReader("presets/" + value));

                    inputReader.lines().forEach(line -> {
                        GradientNode node = GradientNode.fromString(line, pi);
                        inputNodes.add(node);
                    });

                    inputReader.close();

                    if(inputNodes.size() < 2)
                        return;

                    nodes.clear();
                    nodes.addAll(inputNodes);
                    selectedNode = inputNodes.get(0);
                    pi.paint();

                }catch(IOException ie)
                {
                    ie.printStackTrace();
                }
            }
            case SAVE -> {
                String filename = (String)JOptionPane.showInputDialog(
                        parent,
                        "Enter preset file name",
                        "Preset save form",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
                if(filename == null)
                    return;
                try{
                    BufferedWriter outputWriter = new BufferedWriter(new FileWriter("presets/" + filename + ".txt"));
                    for(GradientNode node: nodes)
                    {
                        outputWriter.write(node.toString());
                        outputWriter.newLine();
                    }

                    outputWriter.flush();
                    outputWriter.close();
                }catch(IOException ie)
                {
                    ie.printStackTrace();
                }

            }
            default -> {
                throw new RuntimeException("Error occurred in color gradient editor");
            }
        }
    }

    @Override
    public int compareTo(GradientNodeLine o) {
        if (linePosition > o.linePosition)
            return 1;
        else if (linePosition == o.linePosition)
            return 0;
        else
            return -1;
    }
}
