package Noise;

import Noise.Color.GradientColorEditorPanel;

import java.awt.*;

public class ColorProvider {
    private static final ColorProvider provider = new ColorProvider();

    public static int[][] COLORS;
    private static GradientColorEditorPanel editor;

    PaintInterface pi;

    private ColorProvider()
    {
        editor = new GradientColorEditorPanel(() -> {
            System.out.println("Default image update");
        });
        COLORS = editor.getUpdatedColorArray(255);
    }

    public static ColorProvider getInstance(){
        return provider;
    }

    public void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
        editor.setColorUpdateInterface(() -> {
            System.out.println("image update ex");
            COLORS = editor.getUpdatedColorArray(255);
            pi.paint();
        });
    }

    public void showColorEditor()
    {
        editor.showFrame();
    }
}
