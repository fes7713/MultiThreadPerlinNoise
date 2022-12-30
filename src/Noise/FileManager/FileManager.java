package Noise.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    public static String askForFileName(Component parent, String message, String title)
    {
        return (String) JOptionPane.showInputDialog(
                parent,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
    }

    public static void writeStringToFile(String data, String filename)
    {
        if(filename == null)
            return;
        try{
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename));

            outputWriter.write(data);
            outputWriter.flush();
            outputWriter.close();
        }catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }

    public static void writeStringToFile(String data, String folder, String filename, String extension)
    {
        writeStringToFile(data, folder + "/" + filename + "." + extension);
    }

    public static String nextAvailableFileNameIndex(String filename, String extension)
    {
        int cnt = 0;
        String name = filename + ++cnt + "." + extension;

        while(new File(filename).exists())
            name = filename + ++cnt + "." + extension;
        return name;
    }
}
