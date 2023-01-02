package Noise;

import Noise.Color.GradientColorEditorPanel;

public class ColorProvider {
    private static int COLOR_LEVEL = 255;
    private static final ColorProvider provider = new ColorProvider();

    public static int[][] COLORS;
    private static GradientColorEditorPanel editor;

    PaintInterface pi;

    private ColorProvider()
    {
        editor = new GradientColorEditorPanel(() -> {
            System.out.println("Default image update");
        });
        COLORS = editor.getUpdatedColorArray(COLOR_LEVEL);
    }

    public static ColorProvider getInstance(){
        return provider;
    }

    public void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
        editor.setColorUpdateInterface(() -> {
            System.out.println("image update ex");
            COLORS = editor.getUpdatedColorArray(COLOR_LEVEL);
            pi.paint();
        });
    }

    public void showColorEditor()
    {
        editor.showFrame();
    }

    public void setColorLevel(int colorLevel)
    {
        if(colorLevel < 2)
            throw new IllegalArgumentException("Illegal color level");

        COLOR_LEVEL = colorLevel;
        COLORS = editor.getUpdatedColorArray(COLOR_LEVEL);
        pi.paint();
    }
}
