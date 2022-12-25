package Noise.Color;

import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class GradientColorPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private final List<GradientNode> nodes;
    private BufferedImage bi;
    private int[] colors;
    private GradientNode selectedNode;
    private PaintInterface pi;
    private boolean hold;

    public GradientColorPanel(PaintInterface pi)
    {
        this.pi = pi;
        hold = false;
        nodes = new ArrayList<>();
        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        addMouseListener(this);
        addMouseMotionListener(this);

        nodes.add(new GradientNode(Color.WHITE, 0F, this::repaint));
        nodes.add(new GradientNode(Color.BLACK, 1F, this::repaint));
        selectedNode = nodes.get(0);
        updateColorArray(255);
    }

    public void setPaintInterface(PaintInterface pi)
    {
        System.err.println("Color panel paint interface has been updated");
        this.pi = pi;
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
                    bi.setRGB(i, j, colors[i]);
                }
            }
        }
        else{
            updateColorArray(height);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bi.setRGB(i, j, colors[j]);
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

    public int[] getUpdatedColorArray(int size)
    {
        updateColorArray(size);
        return colors;
    }

    public int[] getColorArray(){
        return colors;
    }

    private void updateColorArray(int size)
    {
        if(nodes.size() < 2)
            throw new RuntimeException("Node list cannot be less than 2");
        Collections.sort(nodes);
        List<GradientNode> gradationNodes = new ArrayList<>(nodes);

        if(gradationNodes.get(0).getPosition() != 0)
            gradationNodes.add(0, new GradientNode(gradationNodes.get(0).getColor(), 0, this::repaint));

        if(gradationNodes.get(gradationNodes.size() - 1).getPosition() != 1)
            gradationNodes.add(new GradientNode(gradationNodes.get(gradationNodes.size() - 1).getColor(), 1, this::repaint));

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

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2)
        {
            for(GradientNode node: nodes)
            {
                if(node.mouseEvent(e, this.getWidth(), this.getHeight(), node == selectedNode))
                {
                    selectedNode = node;
                    return;
                }
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
                repaint();
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
                repaint();
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
                repaint();
            }
            case LOAD -> {
                try{
                    File[] files= new File("presets").listFiles();

                    String[] values = new String[files.length];
                    IntStream.range(0, values.length).forEach(i -> {
                        values[i] = files[i].getName();
                    });

                    Object value = JOptionPane.showInputDialog(this, "Message",
                            "Load presets", JOptionPane.ERROR_MESSAGE,
                            new ImageIcon("icons/preset1.png"), values, values[0]);

                    if(value == null)
                        return;

                    List<GradientNode> inputNodes = new ArrayList<>();
                    BufferedReader inputReader = new BufferedReader(new FileReader("presets/" + value));

                    inputReader.lines().forEach(line -> {
                        GradientNode node = GradientNode.fromString(line, this::repaint);
                        inputNodes.add(node);
                    });

                    inputReader.close();

                    if(inputNodes.size() < 2)
                        return;

                    nodes.clear();
                    nodes.addAll(inputNodes);
                    selectedNode = inputNodes.get(0);
                    repaint();

                }catch(IOException ie)
                {
                    ie.printStackTrace();
                }
            }
            case SAVE -> {
                String filename = (String)JOptionPane.showInputDialog(
                        this,
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
