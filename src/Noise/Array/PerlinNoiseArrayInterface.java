package Noise.Array;

import Noise.FastNoise;
import Noise.PaintInterface;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

        int cnt = 0;
        String filename = "noise" + ++cnt + ".csv";

        while(new File(filename).exists())
            filename = "noise" + ++cnt + ".csv";

        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename));
            outputWriter.write(sb.toString());
            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveNormalArrayToFile(float[][] normalMap){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < normalMap[0].length; i++) {
            sb.append(normalMap[0][i]);
            sb.append("\n");
        }

        int cnt = 0;
        String filename = "normal" + ++cnt + ".csv";

        while(new File(filename).exists())
            filename = "normal" + ++cnt + ".csv";

        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename));
            outputWriter.write(sb.toString());
            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveZoomTable(FastNoise fn)
    {
        StringBuilder sb1 = new StringBuilder();

        sb1.append("Zoom\\Resolution,");
        for (int i = -2; i < 16; i++) {
            sb1.append(i);
            sb1.append(",");
        }
        sb1.append("\n");
        for (int i = -10; i < 10; i++) {
            float zoom = (float)Math.pow(1.1, i);
            sb1.append(zoom);
            for (int j = -2; j < 16; j++) {
                float noise = 0;

                for (int k = -2; k < j; k++) {
                    float resolution = (float)Math.pow(2, k);
                    noise += fn.GetNoise((i * zoom) * resolution, (j * zoom) * resolution ) / resolution;
                }
                sb1.append(noise);
                sb1.append(",");
            }
            sb1.append("\n");
        }

        int cnt = 0;
        String filename = "zoomres" + ++cnt + ".csv";

        while(new File(filename).exists())
            filename = "zoomres" + ++cnt + ".csv";

        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filename));
            outputWriter.write(sb1.toString());
            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
