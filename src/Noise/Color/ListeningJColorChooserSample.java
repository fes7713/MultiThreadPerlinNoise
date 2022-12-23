package Noise.Color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ListeningJColorChooserSample {

    public static void main(String args[]) {
        final JColorChooser colorChooser = new JColorChooser(Color.BLUE);

        JDialog dialog = JColorChooser.createDialog(null, "Title", false, colorChooser,
                e -> {
                System.out.println("Okay ");
                    Color newForegroundColor = colorChooser.getColor();
                    System.out.println("-- 選択された色 --");
                    System.out.println("R:" + newForegroundColor.getRed());
                    System.out.println("G:" + newForegroundColor.getGreen());
                    System.out.println("B:" + newForegroundColor.getBlue());
                    },
                e -> {
                System.out.println("Cancel");
                    Color newForegroundColor = colorChooser.getColor();
                    System.out.println("-- 選択された色 --");
                    System.out.println("R:" + newForegroundColor.getRed());
                    System.out.println("G:" + newForegroundColor.getGreen());
                    System.out.println("B:" + newForegroundColor.getBlue());
                    }
            );

        ColorSelectionModel model = colorChooser.getSelectionModel();
        ChangeListener changeListener = changeEvent -> {
            Color newForegroundColor = colorChooser.getColor();
            System.out.println("-- 選択された色 --");
            System.out.println("R:" + newForegroundColor.getRed());
            System.out.println("G:" + newForegroundColor.getGreen());
            System.out.println("B:" + newForegroundColor.getBlue());
//            label.setForeground(newForegroundColor);
        };
        model.addChangeListener(changeListener);
        model.setSelectedColor(Color.BLUE);

        dialog.setVisible(true);
    }
}