package map.Noise.Array;

import map.Noise.ChunkProvider;
import map.Noise.ColorProvider;
import map.Noise.FastNoise;
import map.Noise.PaintInterface;

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
        this.left = left;
        this.top = top;
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
        this.left = left;
        this.top = top;
        generateFallOffMap();
    }

    @Override
    public void setCenter(float centerX, float centerY) {
        this.centerX = centerX / chunkProvider.getWidthArrayDivider();
        this.centerY = centerY / chunkProvider.getHeightArrayDivider();
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

    public void initNoiseMap(float resolution)
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                noiseMap[i][j] = fn.GetNoise((i * zoom  + left) * resolution, (j * zoom + top) * resolution ) / resolution;
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
        generateFallOffMap(0, 0, width, height);
    }

    public void generateFallOffMap(int startLeft, int startTop, int lengthWidth, int lengthHeight)
    {
        float MASK_SIZE = chunkProvider.getMaskSize() * 1000000;
        float MASK_SHADOW = chunkProvider.getMaskShadow();
        float x;
        float y;
        float value;
        for (int i = startLeft; i + 1 < startLeft + lengthWidth; i+= 2) {
            for (int j = startTop; j + 1 < startTop + lengthHeight; j+=2) {
                x = i * zoom + left - centerX;
                y = j * zoom + top - centerY;
                value = (float)Math.exp(- (
                        x * x
                        +
                        y * y)
                        / MASK_SIZE);
                fallOffMap[i][j] = value;
                fallOffMap[i + 1][j] = value;
                fallOffMap[i][j + 1] = value;
                fallOffMap[i + 1][j + 1] = value;
            }
        }

        if(lengthWidth % 2 == 1)
        {
            x = (lengthWidth + startLeft - 1) * zoom + left - centerX;
            for (int j = startTop; j < lengthHeight + startTop; j++) {
                y = j * zoom + top - centerY;
                value = (float)Math.exp(- (
                        x * x
                        +
                        y * y)
                        / MASK_SIZE);
                fallOffMap[width - 1][j] = value;
            }
        }

        if(lengthHeight % 2 == 1)
        {
            y = (lengthHeight + startTop - 1) * zoom + top - centerY;
            for (int i = startLeft; i < lengthWidth + startLeft; i++) {
                x = i * zoom + left - centerX;
                value = (float)Math.exp(- (
                        x * x
                        +
                        y * y)
                        / MASK_SIZE);
                fallOffMap[i][height - 1] = value;
            }
        }
    }

    // Direction
    // ->->->
    @Override
    public float[][] pushMaskLeft(int amount, float[][] incomingPixels, float[][] buffer) {
        if(amount == 0)
            return null;
        if(amount > 0){
            for(int i = 0; i < amount; i++)
            {
                for(int j = 0; j < height; j++)
                {
                    buffer[i][j] = fallOffMap[width - amount + i][j];
                }
            }

            for(int i = 0; i < width - amount; i++)
            {
                for(int j = 0; j < height; j++)
                {
                    fallOffMap[width - i - 1][j] = fallOffMap[width - amount - i - 1][j];
                }
            }

            if(incomingPixels == null)
            {
                generateFallOffMap(width - amount, 0, amount, height);
                incomingPixels = new float[amount][height];
            }
            else{
                for(int i = 0; i < amount; i++)
                {
                    for(int j = 0; j < height; j++)
                    {
                        fallOffMap[width - amount + i][j] = incomingPixels[i][j];
                    }
                }
            }
        }
        else{
            System.out.println("Not implemented");
        }

        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < height; j++) {
                incomingPixels[i][j] = buffer[i][j];
            }
        }

        return incomingPixels;
    }

    // Direction
    // ->->->
    @Override
    public float[][] pushMaskTop(int amount, float[][] incomingPixels, float[][] buffer) {
        return new float[0][];
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

    public void convertData()
    {
        float NOISE_COEFFICIENT = chunkProvider.getNoiseCoefficient();
        float NOISE_SHIFT = chunkProvider.getNoiseShift();
        float NORMAL_COEFFICIENT = chunkProvider.getNormalCoefficient();
        float NORMAL_SHIFT = chunkProvider.getNormalShift();
        float MASK_SHADOW = chunkProvider.getMaskShadow();
        for(int i = 0; i < width - 1; i++)
        {
            for(int j = 0; j < height - 1; j++)
            {
                convNoiseMap[i][j] = (float)convertNoise(noiseMap[i][j], NOISE_COEFFICIENT, NOISE_SHIFT);
                convNormalMap[i][j] = (float)convertNormal(normalMap[i][j], NORMAL_COEFFICIENT, NORMAL_SHIFT);
            }
        }

        for(int i = 0; i < width; i++)
        {
            convNoiseMap[i][height - 1] = (float)convertNoise(noiseMap[i][height - 1], NOISE_COEFFICIENT, NOISE_SHIFT);
            convNormalMap[i][height - 1] = convNormalMap[i][height - 2];
        }

        for(int i = 0; i < height; i++)
        {
            convNoiseMap[width - 1][i] = (float)convertNoise(noiseMap[width - 1][i], NOISE_COEFFICIENT, NOISE_SHIFT);
            convNormalMap[width - 1][i] = convNormalMap[width - 2][i];
        }
    }

    public void updateImage(PaintInterface pi)
    {
        int[][] colors = colorProvider.getColors();
        int length = colors.length;
        float fallOff;
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                fallOff = length * fallOffMap[i][j] * fallOffMap[i][j];

                bi.setRGB(i, j, colors[
                        (int)(convNormalMap[i][j]  * fallOffMap[i][j] * length)
                        ][
                        (int)(convNoiseMap[i][j]  * fallOff)
                        ]);
            }
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