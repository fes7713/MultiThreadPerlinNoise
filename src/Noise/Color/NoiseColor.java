package Noise.Color;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class NoiseColor {
    public static final Color TrenchColor = new Color(14,27,61);
    public static final Color DeepColor = new Color(28,50,100);
    public static final Color ShallowColor = new Color(17,81,145);
    public static final Color ShoreColor = new Color(24,129,174);
    public static final Color SandColor = new Color(201,168,143);
    public static final Color CrayColor = new Color(156, 116, 101);
    public static final Color GrassColor = new Color(78,112,87);
    public static final Color ForestColor = new Color(32,68,57);
    public static final Color RockColor = new Color(80,50,50);
    public static final Color SnowColor = new Color(222,228,240);
    public static final Color CityColor = Color.GRAY;

    private static int nColorLevel = 255;

    public NoiseColor() {
    }

    public void showColorPalette()
    {
        final JColorChooser colorChooser = new JColorChooser(Color.BLUE);

        JDialog dialog = JColorChooser.createDialog(null, "Title", false, colorChooser,
                e -> {
                    System.out.println("Okay ");
                    Color newForegroundColor = colorChooser.getColor();
                    colorUpdate(newForegroundColor);
                },
                e -> {
                    System.out.println("Cancel");
                    Color newForegroundColor = colorChooser.getColor();
                    colorUpdate(newForegroundColor);
                }
        );

        ColorSelectionModel model = colorChooser.getSelectionModel();
        ChangeListener changeListener = changeEvent -> {
            Color newForegroundColor = colorChooser.getColor();
            colorUpdate(newForegroundColor);
        };
        model.addChangeListener(changeListener);
        model.setSelectedColor(Color.BLUE);

        dialog.setVisible(true);
    }

    public void colorUpdate(Color newColor)
    {
        System.out.println("R:" + newColor.getRed());
        System.out.println("G:" + newColor.getGreen());
        System.out.println("B:" + newColor.getBlue());
    }

    public static void main(String[] args) {
//        Color defaultColor = new Color(65,105,225);
//        Color selectedColor = JColorChooser.showDialog(null, "色を選択してね♪", defaultColor);
//        if(selectedColor == null) {
//            System.out.println("選択されなかったよ(TT)");
//        }else {
//            System.out.println("-- 選択された色 --");
//            System.out.println("R:" + selectedColor.getRed());
//            System.out.println("G:" + selectedColor.getGreen());
//            System.out.println("B:" + selectedColor.getBlue());
//        }
        new NoiseColor().showColorPalette();
    }
}
