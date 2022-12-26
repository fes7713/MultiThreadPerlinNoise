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

public class GradientNodeLine {
    private float linePosition;
    private final List<GradientNode> nodes;
    private GradientNode selectedNode;
    private int[] colors;

    boolean hold;

    PaintInterface pi;

    private static int LINE_THICKNESS = 15;

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
        updateColorArray(255);
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
        if(linePosition < 0 || 1 < linePosition)
        {
            throw new IllegalArgumentException("Line position should be between 0 and 1");
        }
        this.linePosition = linePosition;
    }

    public void setPaintInterface(PaintInterface pi)
    {
        System.err.println("Color panel paint interface has been updated");
        this.pi = pi;
    }

    public int[] getUpdatedColorArray(int size)
    {
        updateColorArray(size);
        return colors;
    }

    public int[] getColorArray(){
        return colors;
    }

    // TODO private later
    public  void updateColorArray(int size)
    {
        if(nodes.size() < 2)
            throw new RuntimeException("Node list cannot be less than 2");
        Collections.sort(nodes);
        List<GradientNode> gradationNodes = new ArrayList<>(nodes);

        if(gradationNodes.get(0).getPosition() != 0)
            gradationNodes.add(0, new GradientNode(gradationNodes.get(0).getColor(), 0, pi));

        if(gradationNodes.get(gradationNodes.size() - 1).getPosition() != 1)
            gradationNodes.add(new GradientNode(gradationNodes.get(gradationNodes.size() - 1).getColor(), 1, pi));

        colors= new int[size];
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

                colors[cnt++] = Color.getHSBColor(newhsvvals[0], newhsvvals[1], newhsvvals[2]).getRGB();
            }
        }

        if(pi != null)
            pi.paint();
    }

    public void paint(Graphics2D g2d, int width, int height)
    {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        updateColorArray(height);

        for (int i = 0; i < LINE_THICKNESS; i++) {
            for (int j = 0; j < height; j++) {
                bi.setRGB(i + width / 2 - LINE_THICKNESS / 2, j, colors[j]);
            }
        }
        selectedNode.paint((Graphics2D) bi.getGraphics(), 0, width, height, true);
        for(GradientNode node: nodes)
            if(node != selectedNode)
                node.paint((Graphics2D) bi.getGraphics(), 0, width, height);
        g2d.drawImage(bi, 0, 0, width, height, null);


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

    public void mouseDragged(MouseEvent e, int width, int height)
    {
        if(hold)
        {
            int x = e.getX();
            int y = e.getY();

            selectedNode.setPosition(y / (float)height);
            pi.paint();
        }
    }

    public void action(ActionEvent e, JPanel parent)
    {
        System.out.println(e.getActionCommand());

        switch (ColorEditorAction.valueOf(e.getActionCommand()))
        {
            case ADD_CELL -> {
                GradientNode newNode;
                if(selectedNode.getPosition() < 0.9F)
                    newNode = new GradientNode(selectedNode, selectedNode.getPosition() + 0.05F);
                else
                    newNode =new GradientNode(selectedNode, selectedNode.getPosition() - 0.05F);

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
}
