package Noise.Color;

import Noise.FileManager.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GradientColorPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
    private BufferedImage bi;
    private ColorUpdateInterface cui;
    private boolean hold;
    private final List<GradientNodeLine> lines;
    private GradientNodeLine selectedLine;
    private int[][] array2D;

    public GradientColorPanel(ColorUpdateInterface cui)
    {
        this.cui = () -> {
            this.repaint();
            if(cui != null)
                cui.update();
        };

        System.err.println("Color panel paint interface has been set");
        hold = false;

        bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        addMouseListener(this);
        addMouseMotionListener(this);

        lines = new ArrayList<>();
        lines.add(new GradientNodeLine(0.2F, this.cui::update));
        lines.add(new GradientNodeLine(0.4F, this.cui::update));
        lines.add(new GradientNodeLine(0.7F, this.cui::update));

        selectedLine = lines.get(0);
    }

    public void setColorUpdateInterface(ColorUpdateInterface cui)
    {
        System.err.println("Color panel color update interface has been updated");
        this.cui = () -> {
            this.repaint();
            if(cui != null)
                cui.update();
        };

        for (GradientNodeLine line: lines)
            line.setPaintInterface(this.cui::update);
    }

    //    public void set
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = this.getWidth();
        int height = this.getHeight();
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        updateColorArray(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bi.setRGB(i, j, array2D[i][j]);
            }
        }

        Graphics2D g2d = (Graphics2D)bi.getGraphics();
        for(GradientNodeLine line: lines)
            line.paint(g2d, width, height, line==selectedLine);
        g.drawImage(bi, 0, 0, width, height, null);
    }



    public int[][] getColors()
    {
        return array2D;
    }
    public int[][] getUpdatedColor2DArray(int width, int height)
    {
        updateColorArray(width, height);
        return array2D;
    }

    private void updateColorArray(int width, int height){
        if(lines.size() < 1)
            throw new RuntimeException("Line list cannot be less than 1");
        Collections.sort(lines);

        Color[][] tempArray2D = new Color[lines.size()][height];
        for(int i = 0; i < lines.size(); i++)
        {
            lines.get(i).updateColorArray(height, false);
            tempArray2D[i] = lines.get(i).getColorArray();
        }

        array2D = new int[width][height];

        if(lines.get(0).getPosition() > 0)
        {
            for (int i = 0; i < height; i++)
            {
                int color = tempArray2D[0][i].getRGB();
                for (int j = 0; j / (float)width < lines.get(0).getPosition(); j++) {
                    array2D[j][i] = color;
                }
            }
        }

        int size = lines.size();
        for (int i = (int)(width * lines.get(size - 1).getPosition()); i < width; i++) {
            for (int j = 0; j < height; j++)
            {
                array2D[i][j] = tempArray2D[size - 1][j].getRGB();
            }
        }

        for (int p = 0; p < height; p++) {
            int cnt = (int)(lines.get(0).getPosition() * width);
            for(int i = 1; i < size; i++) {
                float[] prehsbvals = new float[3];
                float[] hsbvals = new float[3];

                Color.RGBtoHSB(tempArray2D[i - 1][p].getRed(), tempArray2D[i - 1][p].getGreen(), tempArray2D[i - 1][p].getBlue(), prehsbvals);
                Color.RGBtoHSB(tempArray2D[i][p].getRed(), tempArray2D[i][p].getGreen(), tempArray2D[i][p].getBlue(), hsbvals);

                if(prehsbvals[1]== 0)
                    prehsbvals[0] = hsbvals[0];
                if(hsbvals[1]== 0)
                    hsbvals[0] = prehsbvals[0];

                for (int j = 0; cnt / (float) width < lines.get(i).getPosition() && cnt < width; j++) {
                    float[] newhsvvals = GradientInterface.interpolateColor(
                            width,
                            cnt,
                            prehsbvals,
                            hsbvals,
                            lines.get(i).getPosition(),
                            lines.get(i - 1).getPosition(),
                            i
                    );
                    array2D[cnt++][p] = Color.getHSBColor(newhsvvals[0], newhsvvals[1], newhsvvals[2]).getRGB();
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int width = this.getWidth();
        int height = this.getHeight();

        selectedLine.mouseClicked(e, width, height);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int width = this.getWidth();
        int height = this.getHeight();

        selectedLine.mousePressed(e, width, height);

        for(GradientNodeLine line: lines)
        {
            if(line == selectedLine)
                continue;
            if(line.contains(e, line.getPosition() * width, height, false))
            {
                selectedLine = line;
                selectedLine.mousePressed(e, width, height);
                hold = true;
                cui.update();
                return;
            }
        }
        if(selectedLine.contains(e, selectedLine.getPosition() * width, height, true))
        {
            hold = true;
            return;
        }
        hold = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int width = this.getWidth();
        int height = this.getHeight();

        selectedLine.mouseDragged(e, width, height);

        if (hold) {
            int x = e.getX();
            int y = e.getY();

            selectedLine.setPosition(x / (float) getWidth());
            cui.update();
        }

    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for(GradientNodeLine line: lines)
        {
            sb.append(line.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public void loadFromString(String data)
    {
        List<GradientNodeLine> inputLines = new ArrayList<>();

        Stream.of(data.split("\n")).forEach(str -> {
            GradientNodeLine line = GradientNodeLine.fromString(str, this.cui::update);
            inputLines.add(line);
        });

        if(inputLines.size() < 1)
            return;

        lines.clear();
        lines.addAll(inputLines);
        selectedLine = inputLines.get(0);
        cui.update();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ColorEditorAction action = ColorEditorAction.valueOf(e.getActionCommand());

        switch (action)
        {
            case ADD_CELL -> {
                selectedLine.action(e, this);
            }
            case ADD_ROW -> {
                selectedLine = GradientInterface.addComponent(this, lines, selectedLine.clone(), selectedLine);
                cui.update();
            }
            case DEL_CELL -> {
                selectedLine.action(e, this);
            }
            case DEL_ROW -> {
                selectedLine = GradientInterface.deleteComponent(lines, selectedLine);
                cui.update();
            }
            case SAVE -> {
                System.out.println("save");
                String filename = FileManager.askForFileName(this, "Enter preset file name", "Preset save form");
                FileManager.writeStringToFile(this.toString(), "presets", filename, "txt");
            }
            case LOAD -> {
                System.out.println("Load");
                String filename = FileManager.askForFileNameFromListInDir(this,
                        "presets",
                        "Message",
                        "Load presets");

                FileManager.loadStringFromFile("presets", filename, this::loadFromString);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
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
}
