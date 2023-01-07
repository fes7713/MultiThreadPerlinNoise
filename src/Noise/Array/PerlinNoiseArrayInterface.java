package Noise.Array;

import Noise.FastNoise;
import Noise.FileManager.FileManager;
import Noise.PaintInterface;

import java.awt.image.BufferedImage;

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

    float convertNoise(float noise, float NOISE_COEFFICIENT, float NOISE_SHIFT);
    float convertNormal(float normal, float NORMAL_COEFFICIENT, float NORMAL_SHIFT);
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
}