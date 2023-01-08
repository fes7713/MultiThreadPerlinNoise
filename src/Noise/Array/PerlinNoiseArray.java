package Noise.Array;

import Noise.ChunkProvider;
import Noise.ColorProvider;
import Noise.FastNoise;
import Noise.PaintInterface;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class PerlinNoiseArray implements PerlinNoiseArrayInterface{
    private final ChunkProvider chunkProvider;
    private final ColorProvider colorProvider;

    private float[][] noiseMap;
    private float[][] normalMap;
    private float[][] convNoiseMap;
    private float[][] convNormalMap;
    private float[][] fallOffMap;

    private float left;
    private float top;
    private int height;
    private int width;

    private float centerX;
    private float centerY;

    private final FastNoise fn;
    private BufferedImage bi;
    private float zoom;

    public PerlinNoiseArray(ChunkProvider chunkProvider, ColorProvider colorProvider, FastNoise fn, float left, float top, int width, int height, float zoom, float centerX, float centerY){
        this.chunkProvider = chunkProvider;
        this.colorProvider = colorProvider;

        this.zoom = zoom;
        this.fn = fn;
        this.left = left * zoom;
        this.top = top * zoom;
        this.width = width;
        this.height = height;
        this.centerX = centerX;
        this.centerY = centerY;

        noiseMap = new float[width][height];
        normalMap = new float[width][height];
        fallOffMap = new float[width][height];
        convNoiseMap = new float[width][height];
        convNormalMap = new float[width][height];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        generateFallOffMap();
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        dimensionChanged();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        dimensionChanged();
    }

    public void reuse(float left, float top, float zoom)
    {
        this.zoom = zoom;
        this.left = left * zoom;
        this.top = top * zoom;
        generateFallOffMap();
    }

    @Override
    public void setCenter(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        generateFallOffMap();
    }

    private void dimensionChanged()
    {
        noiseMap = new float[width][height];
        normalMap = new float[width][height];
        fallOffMap = new float[width][height];
        convNoiseMap = new float[width][height];
        convNormalMap = new float[width][height];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        generateFallOffMap();
    }

    public void initNoiseMap()
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                noiseMap[i][j] = 0;
            }
        }
    }

    public void increaseResolution(float resolution)
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                noiseMap[i][j] += fn.GetNoise((i * zoom  + left) * resolution, (j * zoom + top) * resolution ) / resolution;
            }
        }
    }

    public void generateNormalMap()
    {
        Vector3f light = new Vector3f(
                chunkProvider.getLightingX(), chunkProvider.getLightingY(), chunkProvider.getLightingZ());
        for(int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                float normal = lightIntensity(
                        zoom, 0, noiseMap[i + 1][j] - noiseMap[i][j],
                        0, zoom, noiseMap[i][j + 1] - noiseMap[i][j],
                        light);
                normalMap[i][j] = (normal + 1) * 4096 + 125;

            }
        }
    }

    public void generateFallOffMap()
    {
        float MASK_SIZE = chunkProvider.getMaskSize() * 1000000;
        float MASK_SHADOW = chunkProvider.getMaskShadow();
        int[][] colors = colorProvider.getColors();
        float fallOff = 1;
        int length = colors.length;

        float x;
        float y;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                x = i  * zoom + left - centerX;
                y = j * zoom + top - centerY;
                fallOffMap[i][j] = (float)Math.exp(- (
                        x * x
                        +
                        y * y)
                        / MASK_SIZE);

                fallOff = length * fallOffMap[i][j] * fallOffMap[i][j];
//                for(int k = 0; k < MASK_SHADOW; k++)
//                    fallOff *= fallOffMap[i][j];

                bi.setRGB(i, j, colors[
                        (int)(convNormalMap[i][j]  * fallOffMap[i][j] * length)
                        ][
                        (int)(convNoiseMap[i][j]  * fallOff)
                        ]);
            }
        }
    }

    public double convertNoise(float noise, float NOISE_COEFFICIENT, float NOISE_SHIFT)
    {
//        return 1 - (float)Math.pow(2.75, -(noise + 0.75) * (noise + 0.75));
//        return (int)(Math.atan(100 * noise / 67) * 80) + 127;
//        return (float)(Math.atan( (noise - NOISE_SHIFT) * NOISE_COEFFICIENT) / Math.PI + 0.5);
        return (1 / (1 + Math.exp(-NOISE_COEFFICIENT * (noise - NOISE_SHIFT))));
    }

    public double convertNormal(float normal, float NORMAL_COEFFICIENT, float NORMAL_SHIFT)
    {
//        float t = 2.5F;
//        int p = 52;
//        int s = 32;
//        return (int)(Math.atan((normal - t * p) / s) * t * s + t * p);
//        return (int)(Math.atan((normal - 125) / 32F) * 80 + 125);
//        return (float)(Math.atan(normal) / Math.PI + 0.5);
//        return (float)(Math.atan( (normal - NORMAL_SHIFT) * NORMAL_COEFFICIENT) / Math.PI + 0.5);
        return (1 / (1 + Math.exp(-NORMAL_COEFFICIENT * (normal - NORMAL_SHIFT))));
    }

    public void updateImage(PaintInterface pi)
    {
        float NOISE_COEFFICIENT = chunkProvider.getNoiseCoefficient();
        float NOISE_SHIFT = chunkProvider.getNoiseShift();
        float NORMAL_COEFFICIENT = chunkProvider.getNormalCoefficient();
        float NORMAL_SHIFT = chunkProvider.getNormalShift();
        float MASK_SHADOW = chunkProvider.getMaskShadow();
        int[][] colors = colorProvider.getColors();
        float fallOff = 1;
        int length = colors.length;
        for(int i = 0; i < width - 1; i++)
        {
            for(int j = 0; j < height - 1; j++)
            {
                fallOff = 1;
                for(int k = 0; k < MASK_SHADOW; k++)
                    fallOff *= fallOffMap[i][j];

                convNoiseMap[i][j] = (float)convertNoise(noiseMap[i][j], NOISE_COEFFICIENT, NOISE_SHIFT);
                convNormalMap[i][j] = (float)convertNormal(normalMap[i][j], NORMAL_COEFFICIENT, NORMAL_SHIFT);

                bi.setRGB(i, j, colors[
                        (int)(convNormalMap[i][j]  * fallOffMap[i][j] * length)
                        ][
                        (int)(convNoiseMap[i][j]  * fallOff * length)
                        ]);
            }
        }

        for(int i = 0; i < width; i++)
        {
            fallOff = 1;
            for(int k = 0; k < MASK_SHADOW; k++)
                fallOff *= fallOffMap[i][height - 1];

            convNoiseMap[i][height - 1] = (float)convertNoise(noiseMap[i][height - 1], NOISE_COEFFICIENT, NOISE_SHIFT);

            bi.setRGB(
                    i,
                    height - 1,
                    colors
                        [colors.length / 2]
                        [(int)(convNoiseMap[i][height - 1] * fallOff * length)]);
        }

        for(int i = 0; i < height; i++)
        {
            fallOff = 1;
            for(int k = 0; k < MASK_SHADOW; k++)
                fallOff *= fallOffMap[width - 1][i];

            convNoiseMap[width - 1][i] = (float)convertNoise(noiseMap[width - 1][i], NOISE_COEFFICIENT, NOISE_SHIFT);

            bi.setRGB(
                    width - 1,
                    i,
                    colors
                        [colors.length / 2]
                        [(int)(convNoiseMap[width - 1][i] * fallOff * length)]);
        }

        if(pi != null)
            pi.paint();
    }

    public void saveMapImage()
    {
        try {
            File outfile = new File("saved.png");
            ImageIO.write(bi, "png", outfile);
        } catch (IOException e) {
            // handle exception
        }
    }

    public BufferedImage getImage()
    {
        return bi;
    }

    public static float lightIntensity(float a1, float a2, float a3, float b1, float b2, float b3, Vector3f light)
    {
        float c1 = a2 * b3 - a3 * b2;
        float c2 = b1 * a3 - b3 * a1;
        float c3 = a1 * b2 - a2 * b1;

        return (float)((c1 * light.x + c2 * light.y + c3 * light.z) / (Math.sqrt(c1 * c1 + c2 * c2 + c3 * c3)));
    }


}