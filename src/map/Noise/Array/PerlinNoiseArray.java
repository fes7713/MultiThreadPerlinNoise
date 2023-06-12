package map.Noise.Array;

import map.Noise.ChunkProvider;
import map.Noise.ColorProvider;
import map.Noise.FastNoise;
import map.Noise.PaintInterface;
import map.VariableChanger;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class PerlinNoiseArray implements PerlinNoiseArrayInterface{
    private final ChunkProvider chunkProvider;
    private final ColorProvider colorProvider;

    private float[][] noiseMap;
    private float[][] normalMap;
    private float[][] convNoiseMap;
    private float[][] fallOffMap;
    private float[][] diffusionMap;
    private float[][] specularMap;

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
        diffusionMap = new float[width][height];
        specularMap = new float[width][height];
        fallOffMap = new float[width][height];
        convNoiseMap = new float[width][height];

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
        specularMap = new float[width][height];
        fallOffMap = new float[width][height];
        convNoiseMap = new float[width][height];
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
        convertData();
        Vector3f light = new Vector3f(
                chunkProvider.getLightingX(), chunkProvider.getLightingY(), chunkProvider.getLightingZ());
        float lightLength = light.length();
        int specularBrightness = chunkProvider.getSpecularBrightness();
        int specularIntensity = chunkProvider.getSpecularIntensity();

        for(int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                Vector3f normal = cross(
                        zoom, 0, (convNoiseMap[i + 1][j] - convNoiseMap[i][j]),
                        0, zoom, (convNoiseMap[i][j + 1] - convNoiseMap[i][j]));

                float normalLength = normal.length();

                float normalDot = normalIntensity(normal, normalLength);
                normalMap[i][j] = normalDot;

                float diffusion = diffusionIntensity(normal, normalLength, light, lightLength);
                diffusionMap[i][j] = diffusion;

                float specular = specularIntensity(normal, normalLength, light, lightLength, specularIntensity);
                specularMap[i][j] = specular;
            }
        }

        for(int i = 0; i < width; i++)
        {
            normalMap[i][height - 1] = normalMap[i][height - 2];
            diffusionMap[i][height - 1] = diffusionMap[i][height - 2];
            specularMap[i][height - 1] = specularMap[i][height - 2];
        }

        for(int i = 0; i < height; i++)
        {
            normalMap[width - 1][i] = normalMap[width - 2][i];
            diffusionMap[width - 1][i] = diffusionMap[width - 2][i];
            specularMap[width - 1][i] = specularMap[width - 2][i];
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

    public double convertNoise(float noise, float NOISE_COEFFICIENT, float NOISE_SHIFT)
    {
//        return 1 - (float)Math.pow(2.75, -(noise + 0.75) * (noise + 0.75));
//        return (int)(Math.atan(100 * noise / 67) * 80) + 127;
//        return (float)(Math.atan( (noise - NOISE_SHIFT) * NOISE_COEFFICIENT) / Math.PI + 0.5);
//        return (1 / (1 + Math.exp(-NOISE_COEFFICIENT * (noise - NOISE_SHIFT))));
//        return 0.6*noise + 0.2;
//        return -NOISE_COEFFICIENT*Math.abs(noise) + NOISE_SHIFT;
//        return Math.abs(NOISE_COEFFICIENT * Math.pow(noise, 3)) + Math.abs(NOISE_COEFFICIENT * noise);
        return (-NOISE_SHIFT * Math.abs(2 / (1 + Math.exp(NOISE_COEFFICIENT * (noise))) - 1) + NOISE_SHIFT) * chunkProvider.getSpecularBrightness();
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
        return NORMAL_COEFFICIENT * (normal + NORMAL_SHIFT);
    }

    public void convertData()
    {
        float NOISE_COEFFICIENT = chunkProvider.getNoiseCoefficient();
        float NOISE_SHIFT = chunkProvider.getNoiseShift();

        for(int i = 0; i < width - 1; i++)
            for(int j = 0; j < height - 1; j++)
                convNoiseMap[i][j] = (float)convertNoise(noiseMap[i][j], NOISE_COEFFICIENT, NOISE_SHIFT);

        for(int i = 0; i < width; i++)
            convNoiseMap[i][height - 1] = (float)convertNoise(noiseMap[i][height - 1], NOISE_COEFFICIENT, NOISE_SHIFT);


        for(int i = 0; i < height; i++)
            convNoiseMap[width - 1][i] = (float)convertNoise(noiseMap[width - 1][i], NOISE_COEFFICIENT, NOISE_SHIFT);
    }

    public void updateImage(PaintInterface pi)
    {
        int[][] colors = colorProvider.getColors();
        int length = colors.length - 1;
        float fallOff;
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                fallOff = length * fallOffMap[i][j] * fallOffMap[i][j] / chunkProvider.getSpecularBrightness();

                int heightIndex;

                if(convNoiseMap[i][j] * fallOff < 0)
                {
                    heightIndex = 0;
                }
                else if(convNoiseMap[i][j] * fallOff < colors[0].length)
                {
                    heightIndex = (int)(convNoiseMap[i][j] * fallOff);
                }
                else{
                    heightIndex = colors[0].length - 1;
                }

                int colorIndex;

//                double normal = convertNormal(normalMap[i][j], chunkProvider.getNormalCoefficient(), chunkProvider.getNormalShift());

                if(normalMap[i][j] < 0)
                {
                    colorIndex = 0;
                }
                else if(normalMap[i][j] < 1)
                {
                    colorIndex = (int)(normalMap[i][j] * colors[0].length);
                }
                else{
                    colorIndex = colors[0].length - 1;
                }

                int color = colors[colorIndex][heightIndex];

                Color c = new Color(color);

                // Diffusion
                Vector3f c1 = new Vector3f(
                        c.getRed() * diffusionMap[i][j],
                        c.getGreen() * diffusionMap[i][j],
                        c.getBlue() * diffusionMap[i][j]
                );

                float ambientIntensity = chunkProvider.getAmbientIntensity();
                // Ambient
                Vector3f c2 = new Vector3f(
                        c1.x + c.getRed() * ambientIntensity,
                        c1.y + c.getGreen() * ambientIntensity,
                        c1.z + c.getBlue() * ambientIntensity
                );

//                Vector3f c2 = new Vector3f(
//                        c.getRed() + c.getRed() * ambientIntensity,
//                        c.getGreen() + c.getGreen() * ambientIntensity,
//                        c.getBlue() + c.getBlue() * ambientIntensity
//                );

                Vector3f c3 = new Vector3f(
                        c2.x + c.getRed() * specularMap[i][j],
                        c2.y + c.getGreen() * specularMap[i][j],
                        c2.z + c.getBlue() * specularMap[i][j]
                );

                Color c4 = new Color(
                        (int)Math.min(Math.max(c3.x, 0), 255),
                        (int)Math.min(Math.max(c3.y, 0), 255),
                        (int)Math.min(Math.max(c3.z, 0), 255)
                );

                bi.setRGB(i, j, c4.getRGB());
//                bi.setRGB(i, j, color);
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

    public static float dot(Vector3f a, Vector3f b, float aLength, float bLength)
    {
        return (a.x * b.x + a.y * b.y + a.z * b.z) / aLength / bLength;
    }

    public static Vector3f cross(float a1, float a2, float a3, float b1, float b2, float b3)
    {
        final float c1 = a2 * b3 - a3 * b2;
        final float c2 = b1 * a3 - b3 * a1;
        final float c3 = a1 * b2 - a2 * b1;

        return new Vector3f(c1, c2, c3);
    }

    public static float normalIntensity(Vector3f normal, float normalLength)
    {
        Vector3f vertical = new Vector3f(0, 0, 1);
        return (normal.x * vertical.x + normal.y * vertical.y + normal.z * vertical.z) / normalLength;
    }

    public static float diffusionIntensity(Vector3f normal, float normalLength, Vector3f light, float lightLength)
    {
        return dot(normal, light, normalLength, lightLength);
    }

    public static float specularIntensity(Vector3f normal, float normalLength, Vector3f light, float lightLength, int specularIntensity)
    {
        Vector3f camera = new Vector3f(0, 0, -1);

        Vector3f normalNormalised = new Vector3f(normal.x / normalLength, normal.y / normalLength, normal.z / normalLength);
        float lightNormalNormalizedDot = dot(light, normalNormalised, lightLength, 1);
//                light.x * normalNormalised.x + light.y * normalNormalised.y + light.z * normalNormalised.z;

        Vector3f reflected1 = new Vector3f(
                light.x - 2 * lightNormalNormalizedDot * normalNormalised.x,
                light.y - 2 * lightNormalNormalizedDot * normalNormalised.y,
                light.z - 2 * lightNormalNormalizedDot * normalNormalised.z);

        float specularDot1 = dot(reflected1, camera, reflected1.length(), 1);
//                (float)((reflected1.x * camera.x + reflected1.y * camera.y + reflected1.z * camera.z)
//                /
//                (Math.sqrt(reflected1.x * reflected1.x + reflected1.y * reflected1.y + reflected1.z * reflected1.z)));

//        if(specularDot1 < 0)
//            return 0;
        return (float)Math.pow(specularDot1, specularIntensity);
    }

    public static void main(String[] args)
    {
        ColorProvider colorProvider = new ColorProvider(null, 256);
        colorProvider.loadColorPreset("PhongRealisticColor1.txt");
        ChunkProvider chunkProvider = new ChunkProvider(colorProvider, null);
        VariableChanger vc = new VariableChanger(chunkProvider, null);
        vc.loadVariable();
        PerlinNoiseArray array = new PerlinNoiseArray(chunkProvider, colorProvider, new FastNoise(), -500, -500, 100, 100, 1, 500, 500);
        int resolutionMin = chunkProvider.getResolutionMin();
        int resolutionMax = chunkProvider.getResolutionMax();

        if(Thread.interrupted())
            return;
        array.initNoiseMap(resolutionMin);

        for (int i = resolutionMin + 1; i < resolutionMax + 4; i++) {
            array.increaseResolution((float)Math.pow(2, i));
        }
        array.generateNormalMap();
        array.updateImage(null);

        File outputfile = new File("image.png");
        try {
            ImageIO.write(array.bi, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}