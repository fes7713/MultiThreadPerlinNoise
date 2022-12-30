package Noise.Array;

import Noise.Color.GradientNodeLine;
import Noise.FastNoise;
import Noise.FileManager.FileManager;
import Noise.PaintInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
}
