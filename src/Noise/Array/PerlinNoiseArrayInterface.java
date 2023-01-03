package Noise.Array;

import Noise.FastNoise;
import Noise.FileManager.FileManager;
import Noise.PaintInterface;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface PerlinNoiseArrayInterface {
    void setLeft(float left);
    void setTop(float top);
    float getLeft();
    float getTop();
    int getHeight();
    void setHeight(int height);
    int getWidth();
    void setWidth(int width);

    void reuse(float left, float top, float zoom);
    void setCenter(float centerX, float centerY);
    void initNoiseMap();
    void increaseResolution(float resolution);
    void generateNormalMap();

    float convertNoise(float noise);
    float convertNormal(float normal);
    void updateImage(PaintInterface pi);

    void saveMapImage();
    BufferedImage getImage();

    static void saveNoiseArrayToFile(float[][] noiseMap){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < noiseMap[0].length; i++) {
            sb.append(noiseMap[0][i]);
            sb.append("\n");
        }

        String filename = FileManager.nextAvailableFileNameIndex("noise", "csv");
        FileManager.writeStringToFile(sb.toString(), filename);
    }

    static void saveNormalArrayToFile(float[][] normalMap){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < normalMap[0].length; i++) {
            sb.append(normalMap[0][i]);
            sb.append("\n");
        }

        String filename = FileManager.nextAvailableFileNameIndex("normal", "csv");
        FileManager.writeStringToFile(sb.toString(), filename);
    }

    static void saveZoomTable(FastNoise fn)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("Zoom\\Resolution,");
        for (int i = -2; i < 16; i++) {
            sb.append(i);
            sb.append(",");
        }
        sb.append("\n");
        for (int i = -10; i < 10; i++) {
            float zoom = (float)Math.pow(1.1, i);
            sb.append(zoom);
            for (int j = -2; j < 16; j++) {
                float noise = 0;

                for (int k = -2; k < j; k++) {
                    float resolution = (float)Math.pow(2, k);
                    noise += fn.GetNoise((i * zoom) * resolution, (j * zoom) * resolution ) / resolution;
                }
                sb.append(noise);
                sb.append(",");
            }
            sb.append("\n");
        }

        String filename = FileManager.nextAvailableFileNameIndex("zoomres", "csv");
        FileManager.writeStringToFile(sb.toString(), filename);
    }

    static String variableToString()
    {
        StringBuilder sb = new StringBuilder();
        List<String> titles = Stream.of("NOISE_COEFFICIENT", "NOISE_SHIFT", "NORMAL_COEFFICIENT", "NORMAL_SHIFT").toList();

        List<Supplier<Float>> getters = new ArrayList<>(
                Arrays.asList(PerlinNoiseArray::getNoiseCoefficient,
                        PerlinNoiseArray::getNoiseShift,
                        PerlinNoiseArray::getNormalCoefficient,
                        PerlinNoiseArray::getNormalShift));

        IntStream.range(0, titles.size())
                .boxed()
                .forEach((index) -> {
                    sb.append(titles.get(index))
                            .append(",")
                            .append(getters.get(index).get())
                            .append("\n");
                });
        return sb.toString();
    }

    static void saveVariables(Component parent)
    {
        String filename = FileManager.askForFileName(parent, "Enter variable file name", "Variable save form");
        FileManager.writeStringToFile(variableToString(), "variables", filename, "txt");
    }

    static boolean loadVariable(String foldername, String filename, VariableChanger vc)
    {
        List<Consumer<Float>> setters = new ArrayList<>(
                Arrays.asList(PerlinNoiseArray::setNoiseCoefficient,
                        PerlinNoiseArray::setNoiseShift,
                        PerlinNoiseArray::setNormalCoefficient,
                        PerlinNoiseArray::setNormalShift));
        if(!FileManager.loadStringFromFile(foldername, filename,
                (data)->{
                    String[] splited = data.split("\n");
                    IntStream.range(0, setters.size())
                            .boxed()
                            .forEach((index) -> {
                                setters.get(index).accept(Float.parseFloat(splited[index].split(",")[1]));
                            });
                }))
        {
            return false;
        }
        if(vc != null)
            vc.updateData();
        return true;
    }

    static void saveDefaultVariables(String foldername, VariableChanger vc)
    {
        Path p = Paths.get(foldername);
        if(!Files.exists(p)) {
            try {
                Files.createDirectories(p);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        PerlinNoiseArray.setNoiseCoefficient(4.0F);
        PerlinNoiseArray.setNoiseShift(0);
        PerlinNoiseArray.setNormalCoefficient(0.03F);
        PerlinNoiseArray.setNormalShift(125);

        FileManager.writeStringToFile(variableToString(), foldername, "default", "txt");
        if(vc != null)
            vc.updateData();
    }

    static void loadDefaultVariables(VariableChanger vc)
    {
        if(!loadVariable("variables", "default.txt", vc))
        {
            saveDefaultVariables("variables", vc);
        }
        loadVariable("variables", "default.txt", vc);
    }
}
