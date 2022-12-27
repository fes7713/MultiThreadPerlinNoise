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
        editor = new GradientColorEditorPanel(null);
        COLORS = editor.getUpdatedColorArray(255);
    }

    public static ColorProvider getInstance(){
        return provider;
    }
    public static ColorProvider getInstance(PaintInterface pi){
        provider.setPaintInterface(() -> {
            COLORS = editor.getUpdatedColorArray(255);
            pi.paint();
        });
        return provider;
    }

    public void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
        editor.setPaintInterface(() -> {
            System.out.println("image update");
            COLORS = editor.getUpdatedColorArray(255);
            pi.paint();
        });
    }

    public void showColorEditor()
    {
        editor.showFrame();
    }

    public static void main(String[] args)
    {
        ColorProvider provider = ColorProvider.getInstance(null);
        provider.showColorEditor();
    }
}
