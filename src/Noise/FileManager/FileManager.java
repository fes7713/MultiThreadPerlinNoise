package Noise.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        if(!Files.exists(Paths.get(folder)))
        {
            try {
                Files.createDirectories(Paths.get(folder));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

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

    public static String askForFileNameFromListInDir(Component parent, String folder, String message, String title)
    {
        File[] files= new File(folder).listFiles();

        String[] values = new String[files.length];
        IntStream.range(0, values.length).forEach(i -> {
            values[i] = files[i].getName();
        });

        Object value = JOptionPane.showInputDialog(parent, message,
                title, JOptionPane.ERROR_MESSAGE,
                new ImageIcon("icons/preset1.png"), values, values[0]);

        return (String)value;
    }

    public static boolean loadStringFromFile(String folder, String filenameWithExtension, FileLoadAction fileAction)
    {
        if(filenameWithExtension == null)
            return false;
        BufferedReader inputReader = null;
        try {
            inputReader = new BufferedReader(new FileReader(folder + "/" + filenameWithExtension));
            String data =  inputReader.lines().collect(Collectors.joining("\n"));
            inputReader.close();
            fileAction.action(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface FileLoadAction{
        void action(String data);
    }
}
