package map.Noise.Color;

import map.Noise.PaintInterface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class GradientNodeLine  implements GradientInterface, Comparable<GradientNodeLine>, Cloneable, Serializable{
    private float position;
    private List<GradientNode> nodes;
    private GradientNode selectedNode;
    private Color[] colors;

    private boolean hold;

    private PaintInterface pi;

    private static final int LINE_THICKNESS = 10;

    public GradientNodeLine(List<GradientNode> nodes, float position, PaintInterface pi) {
        this.nodes = nodes;

        /*/
        This is position of gradient line. Ratio to the available width;
        This field should be between 0 and 1;
         */
        if(nodes.isEmpty())
        {
            nodes.add(new GradientNode(Color.WHITE, 0F, pi));
            nodes.add(new GradientNode(Color.BLACK, 1F, pi));
        }
        this.position = position;

        selectedNode = nodes.get(0);

        updateColorArray(255, true);
        this.pi = pi;
        hold = false;
    }

    public GradientNodeLine(float position, PaintInterface pi) {
        this(new ArrayList<>(), position, pi);
    }

    public float getPosition()
    {
        return position;
    }

    public void setPosition(float position)
    {
        this.position = Math.max(0, Math.min(1, position));
    }

    public void setPaintInterface(PaintInterface pi)
    {
        System.err.println("Color panel paint interface has been updated");
        this.pi = pi;
        for(GradientNode node: nodes)
        {
            node.setPaintInterface(pi);
        }
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
        for (int i = 0; i < size * nodes.get(0).getPosition(); i++) {
            colors[i] = nodes.get(0).getColor();
        }

        for (int i = (int)(size * nodes.get(nodes.size() - 1).getPosition()); i < size; i++) {
            colors[i] = nodes.get(nodes.size() - 1).getColor();
        }

        int cnt = (int)Math.ceil(size * nodes.get(0).getPosition());

        for(int i = 1; i < nodes.size(); i++)
        {
            float[] prehsbvals = nodes.get(i - 1).getHsb();
            float[] hsbvals = nodes.get(i).getHsb();
            if(prehsbvals[1]== 0)
                prehsbvals[0] = hsbvals[0];
            if(hsbvals[1]== 0)
                hsbvals[0] = prehsbvals[0];

            for (int j = 0; cnt / (float)size < nodes.get(i).getPosition() && cnt < size; j++) {
                float[] newhsvvals = GradientInterface.interpolateColor(
                        size,
                        cnt,
                        prehsbvals,
                        hsbvals,
                        nodes.get(i).getPosition(),
                        nodes.get(i - 1).getPosition(),
                        i
                );
                Color color = Color.getHSBColor(newhsvvals[0], newhsvvals[1], newhsvvals[2]);

                colors[cnt++] = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)newhsvvals[3]);
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

        BufferedImage bi = new BufferedImage(length, height, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                bi.setRGB(i, j, GradientInterface.getIntFromColor(colors[j]));
            }
        }
        g2d.drawImage(bi, (int)(position * width) - length / 2, 0, length, height, null);
        selectedNode.paint(g2d, (int)(position * width), height, true);
        for(GradientNode node: nodes)
            if(node != selectedNode)
                node.paint(g2d, (int)(position * width), height);
    }

    @Override
    public void brighter() {
        for(GradientNode node: nodes)
            node.brighter();
    }

    @Override
    public void darker() {
        for(GradientNode node: nodes)
            node.darker();
    }

    public boolean contains(MouseEvent event, float centerX, float height, boolean selected)
    {
        int multiplier = 1;
        if(selected)
            multiplier = 2;

        int x = event.getX();

        float length = LINE_THICKNESS * multiplier;

        if(centerX - length / 2 < x && x < centerX +  length / 2)
        {
            return true;
        }

        for(GradientNode node: nodes)
        {
            if(node.contains(event, centerX, height, node == selectedNode))
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
                if(node.mouseEvent(e, position * width, height, node == selectedNode))
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
            if(node.contains(e, position * width, height, false))
            {
                selectedNode = node;
                hold = true;
                pi.paint();
                return;
            }
        }
        if(selectedNode.contains(e, position * width, height, true))
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

            selectedNode.setPosition(y / (float)height);
            pi.paint();
            return true;
        }
        return false;
    }

    public void action(ActionEvent e, Component parent)
    {
        System.out.println(e.getActionCommand());

        switch (ColorEditorAction.valueOf(e.getActionCommand()))
        {
            case ADD_CELL -> {
                selectedNode = GradientInterface.addComponent(parent, nodes, selectedNode.clone(), selectedNode);
                pi.paint();
            }
            case DEL_CELL -> {
                selectedNode = GradientInterface.deleteComponent(nodes, selectedNode);
                pi.paint();
            }
            default -> throw new RuntimeException("Error occurred in color gradient editor");
        }
    }

    @Override
    public int compareTo(GradientNodeLine o) {
        if (position > o.position)
            return 1;
        else if (position == o.position)
            return 0;
        else
            return -1;
    }

    @Override
    public GradientNodeLine clone() {
        try {
            GradientNodeLine clone = (GradientNodeLine) super.clone();
            clone.nodes = new ArrayList<>();
            for(GradientNode node: nodes)
                clone.nodes.add(node.clone());
            clone.selectedNode = clone.nodes.get(0);
            clone.colors = new Color[colors.length];
            System.arraycopy(colors, 0, clone.colors, 0, colors.length);

            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(position);
        sb.append("===[");
        sb.append(nodes.get(0));
        for(int i = 1; i < nodes.size(); i++)
        {
            sb.append("/");
            sb.append(nodes.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    public static GradientNodeLine fromString(String data, PaintInterface pi)
    {
        data = data.replace("[", "").replace("]", "");
        String[] splitData = data.split("===");
        float position = Float.parseFloat(splitData[0]);
        List<GradientNode> nodes = new ArrayList<>(
                Stream.of(splitData[1].split("/"))
                .map((nodeStr) -> GradientNode.fromString(nodeStr, pi))
                .toList()
        );

        return new GradientNodeLine(nodes, position, pi);
    }
}
