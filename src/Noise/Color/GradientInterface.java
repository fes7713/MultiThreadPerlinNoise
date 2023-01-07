package Noise.Color;

import Noise.FileManager.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface  GradientInterface{
    void setPosition(float position);
    float getPosition();
    boolean contains(MouseEvent event, float centerX, float height, boolean selected);
    void paint(Graphics2D g2d, int width, int height, boolean selected);
    void brighter();
    void darker();
    String toString();

    static int getIntFromColor(Color color){
        return ((color.getAlpha() << 24) & 0xFF000000) |
                ((color.getRed() << 16) & 0x00FF0000) |
                ((color.getGreen() << 8) & 0x0000FF00) |
                (color.getBlue() & 0x000000FF);
    }

    static <E extends GradientInterface> E deleteComponent(List<E> list, E targetComponent)
    {
        if(list.size() <= 1)
            throw new RuntimeException("Component array cannot be less than 1");
        int index = list.indexOf(targetComponent);
        list.remove(targetComponent);
        if(index != 0)
            return list.get(index - 1);
        else
            return list.get(0);
    }

    static <E extends GradientInterface> E addComponent(Component parent, List<E> list, E newComponent, E targetComponent)
    {
        String selectvalues[] = {"Same", "Brighter", "Darker", "Cancel"};

        int select = JOptionPane.showOptionDialog(parent,
                "Select add color option",
                "Add options",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                selectvalues,
                selectvalues[0]
        );

        if(select == 3)
            return targetComponent;
        else if(select == 1)
            newComponent.brighter();
        else if(select == 2)
            newComponent.darker();

        if(newComponent.getPosition() < 0.9F)
            newComponent.setPosition(targetComponent.getPosition() + 0.05F);
        else
            newComponent.setPosition(targetComponent.getPosition() - 0.05F);

        list.add(newComponent);
        return newComponent;
    }

    static float[] interpolateColor(float size, int cnt, float[] prehsbvals, float[] hsbvals, float position, float position2, int i) {
        float interval = position - position2;
        float ratio = (cnt / size - position2) / interval;

        if(Math.abs(prehsbvals[0] - hsbvals[0]) > 0.5)
        {
            if(prehsbvals[0] > hsbvals[0])
                hsbvals[0] += 1;
            else
                prehsbvals[0] += 1;
        }

        float[] newhsvvals = new float[GradientNode.COLOR_HSV_ARRAY_SIZE];
        for (int k = 0; k < GradientNode.COLOR_HSV_ARRAY_SIZE; k++) {
            newhsvvals[k] = prehsbvals[k] * (1 - ratio) + hsbvals[k] * ratio;
        }

        if(newhsvvals[0] > 1)
            newhsvvals[0] -= 1;
        return newhsvvals;
    }

    static void saveCurrentPreset(Component parent, String foldername, GradientColorPanel gcp)
    {
        String filename = FileManager.askForFileName(parent, "Enter preset file name", "Preset save form");
        FileManager.writeStringToFile(gcp.toString(), foldername, filename, "txt");
    }

    static void loadDefaultColors(GradientColorPanel gcp)
    {
        if(!FileManager.loadStringFromFile("presets", "default.txt", gcp::loadFromString))
        {
            String foldername = "presets";
            Path p = Paths.get(foldername);
            if(!Files.exists(p)) {
                try {
                    Files.createDirectories(p);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            FileManager.writeStringToFile(
                    """
                            0.0===[R=255,G=255,B=255,P=0.0/R=0,G=0,B=0,P=1.0]
                            0.5===[R=255,G=255,B=255,P=0.0/R=0,G=0,B=0,P=1.0]
                            0.1===[R=255,G=255,B=255,P=0.0/R=0,G=0,B=0,P=1.0]""",
                    foldername,
                    "default",
                    "txt");
        }
        FileManager.loadStringFromFile("presets", "default.txt", gcp::loadFromString);
    }
}
