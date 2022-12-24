package Noise;

import Noise.Color.ColorUpdateInterface;
import Noise.Color.GradientColorEditorPanel;

import java.awt.*;
import java.util.Arrays;

public class ColorProvider {
    private static final ColorProvider provider = new ColorProvider();

    public static Color[] COLORS;
    static GradientColorEditorPanel editor;

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
            COLORS = editor.getColors();
            pi.paint();
        });
        return provider;
    }

    public static void setColors(Color[] colors)
    {
        COLORS = colors;
    }
    public void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
//        editor.se(pi);
    }

    public Color[] getColors()
    {
        return COLORS;
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
