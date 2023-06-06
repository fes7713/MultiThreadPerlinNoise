package map.Noise;

import map.Noise.Color.GradientColorEditorPanel;

public class ColorProvider {
    private int colorLevel;

    private  int[][] colors;
    private final GradientColorEditorPanel editor;

    private PaintInterface pi;

    public ColorProvider(PaintInterface pi, int colorLevel)
    {
        this.colorLevel = colorLevel;
        editor = new GradientColorEditorPanel(() -> {
            System.out.println("Default image update");
        });
        colors = editor.getUpdatedColorArray(colorLevel);
        setPaintInterface(pi);
    }

    private void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
        editor.setColorUpdateInterface(() -> {
            System.out.println("image update ex");
            colors = editor.getUpdatedColorArray(colorLevel);

            pi.paint();
        });
    }

    public void loadColorPreset(String fileNameWithExtension)
    {
        editor.loadColorPreset(fileNameWithExtension);
    }

    public void showColorEditor()
    {
        editor.showFrame();
    }

    public int[][] getColors()
    {
        return colors;
    }

    public int getColorLevel()
    {
        return colorLevel;
    }

    public void setColorLevel(int colorLevel)
    {
        if(colorLevel < 2)
            throw new IllegalArgumentException("Illegal color level");

        this.colorLevel = colorLevel;
        colors = editor.getUpdatedColorArray(this.colorLevel);
        pi.paint();
    }
}
