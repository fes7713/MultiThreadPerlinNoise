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
    private float[][] convNormalMap;
    private float[][] fallOffMap;
    private float[][] specularMap;
    private float[][] convSpecularMap;

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
        specularMap = new float[width][height];
        convSpecularMap = new float[width][height];
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
        specularMap = new float[width][height];
        convSpecularMap = new float[width][height];
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
//                System.out.println((noiseMap[i + 1][j] - noiseMap[i][j]) * 4096);
                float normal = lightIntensity(
                        zoom, 0, (noiseMap[i + 1][j] - noiseMap[i][j]) * 4096,
                        0, zoom, (noiseMap[i][j + 1] - noiseMap[i][j]) * 4096,
                        light);
                normalMap[i][j] = normal;
            }
        }
        generateSpecularMap();
    }

    public void generateSpecularMap()
    {
        Vector3f light = new Vector3f(
                chunkProvider.getLightingX(), chunkProvider.getLightingY(), chunkProvider.getLightingZ());
        for(int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                float specular = specularIntensity(
                        zoom, 0, (noiseMap[i + 1][j] - noiseMap[i][j]) * 80,
                        0, zoom, (noiseMap[i][j + 1] - noiseMap[i][j]) * 80,
                        light);
                specularMap[i][j] = specular;
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
//        return (1 / (1 + Math.exp(-NORMAL_COEFFICIENT * (normal - NORMAL_SHIFT))));
        return (2 / (1 + Math.exp(-NORMAL_COEFFICIENT * (normal)))) - 1;
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
            normalMap[i][height - 1] = normalMap[i][height - 2];
            specularMap[i][height - 1] = specularMap[i][height - 2];
        }

        for(int i = 0; i < height; i++)
        {
            convNoiseMap[width - 1][i] = (float)convertNoise(noiseMap[width - 1][i], NOISE_COEFFICIENT, NOISE_SHIFT);
            convNormalMap[width - 1][i] = convNormalMap[width - 2][i];
            normalMap[width - 1][i] = normalMap[width - 2][i];
            specularMap[width - 1][i] = specularMap[width - 2][i];
        }
    }

    public void updateImage(PaintInterface pi)
    {
        int[][] colors = colorProvider.getColors();
        int length = colors.length - 1;
        float fallOff;
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                fallOff = length * fallOffMap[i][j] * fallOffMap[i][j];
                int color = colors[
                        length/2
                        ][
                        (int)(convNoiseMap[i][j]  * fallOff)
                        ];
                Color c = new Color(color);
                Color c1 = new Color(
                        (int)Math.max(c.getRed() * convNormalMap[i][j], 0),
                        (int)Math.max(c.getGreen() * convNormalMap[i][j], 0),
                        (int)Math.max(c.getBlue() * convNormalMap[i][j], 0)
                );

                Color c2 = new Color(
                        (int)Math.min(c1.getRed() + c.getRed() * 0.1, 255),
                        (int)Math.min(c1.getGreen() + c.getGreen() * 0.1, 255),
                        (int)Math.min(c1.getBlue() + c.getBlue() * 0.1, 255)
                );

//                Color c3 = new Color(
//                        (int)Math.min(c2.getRed() + 255 * convSpecularMap[i][j], 255),
//                        (int)Math.min(c2.getGreen() + 255 * convSpecularMap[i][j], 255),
//                        (int)Math.min(c2.getBlue() + 255 * convSpecularMap[i][j], 255)
//                );

                Color c3 = new Color(
                        (int)Math.min(c2.getRed() + c.getRed() * specularMap[i][j], 255),
                        (int)Math.min(c2.getGreen() + c.getGreen() * specularMap[i][j], 255),
                        (int)Math.min(c2.getBlue() + c.getBlue() * specularMap[i][j], 255)
                );

//                Color c3 = new Color(
//                        c2.getRed() + (int)Math.max(-c.getRed() * convNormalMap[i][j] * 0.1, 0),
//                        c2.getGreen() + (int)Math.max(-c.getGreen() * convNormalMap[i][j] * 0.1, 0),
//                        c2.getBlue() + (int)Math.max(- c.getBlue() * convNormalMap[i][j] * 0.1, 0)
//                );


//                bi.setRGB(i, j, colors[
//                        length/2
//                        ][
//                        (int)(convNoiseMap[i][j]  * fallOff)
//                        ]);

                bi.setRGB(i, j, c3.getRGB());
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

    public static float specularIntensity(float a1, float a2, float a3, float b1, float b2, float b3, Vector3f light)
    {
        final float c1 = a2 * b3 - a3 * b2;
        final float c2 = b1 * a3 - b3 * a1;
        final float c3 = a1 * b2 - a2 * b1;

        //light.dot(normal);
//        float alpha = (float)((c1 * light.x + c2 * light.y + c3 * light.z) / (Math.sqrt(c1 * c1 + c2 * c2 + c3 * c3)));
//
////        normal.scale(2);
////        normal.scale(alpha);
////        normal.sub(light);
//
//        c1 = c1 * 2 * alpha - light.x;
//        c2 = c2 * 2 * alpha - light.y;
//        c3 = c3 * 2 * alpha - light.z;
//
        Vector3f camera = new Vector3f(0, 0, -1);
//
//        //normal.dot(camera);
//        float specularDot = (float)((c1 * camera.x + c2 * camera.y + c3 * camera.z) / (Math.sqrt(c1 * c1 + c2 * c2 + c3 * c3)));
//        int intensitySpecular = 9;
//
//        if(specularDot < 0)
//            return 0;
//        return (float)Math.pow(specularDot, intensitySpecular);
//        Vector3f lightNegative = new Vector3f(-light.x, -light.y, -light.z);
        float normalLength = (float)Math.sqrt(c1 * c1 + c2 * c2 + c3 * c3);
//        float LightNegativeNormalDot = lightNegative.x * c1 + lightNegative.y * c2 + lightNegative.z * c3;
//
//        float coefficient = LightNegativeNormalDot / (normalLength * normalLength);
//        Vector3f adjustedNormal = new Vector3f(c1 * coefficient, c2 * coefficient, c3 * coefficient);
//        Vector3f reflected = new Vector3f(light.x + 2 * adjustedNormal.x, light.y + 2 * adjustedNormal.y, light.z + 2 * adjustedNormal.z);
//
//        float specularDot = (float)((reflected.x * camera.x + reflected.y * camera.y + reflected.z * camera.z)
//                /
//                (Math.sqrt(reflected.x * reflected.x + reflected.y * reflected.y + reflected.z * reflected.z)));

        // r = f - 2 Dot (f, n_normal)* n_normal

        Vector3f normalNormalised = new Vector3f(c1 / normalLength, c2 / normalLength, c3 / normalLength);
        float lightNormalNormalizedDot = light.x * normalNormalised.x + light.y * normalNormalised.y + light.z * normalNormalised.z;

        Vector3f reflected1 = new Vector3f(
                light.x - 2 * lightNormalNormalizedDot * normalNormalised.x,
                light.y - 2 * lightNormalNormalizedDot * normalNormalised.y,
                light.z - 2 * lightNormalNormalizedDot * normalNormalised.z);

        float specularDot1 = (float)((reflected1.x * camera.x + reflected1.y * camera.y + reflected1.z * camera.z)
                /
                (Math.sqrt(reflected1.x * reflected1.x + reflected1.y * reflected1.y + reflected1.z * reflected1.z)));

        int intensitySpecular = 5;

        if(specularDot1 < 0)
            return 0;
        return (float)Math.pow(specularDot1, intensitySpecular);
    }

    public static void main(String[] args)
    {
        ColorProvider colorProvider = new ColorProvider(null, 256);
        colorProvider.loadColorPreset("PhongRealisticColor1.txt");
        ChunkProvider chunkProvider = new ChunkProvider(colorProvider, null);
        VariableChanger vc = new VariableChanger(chunkProvider, null);
        vc.loadVariable();
        PerlinNoiseArray array = new PerlinNoiseArray(chunkProvider, colorProvider, new FastNoise(), -500, -500, 4000, 2000, 0.1F, 500, 500);
        int resolutionMin = chunkProvider.getResolutionMin();
        int resolutionMax = chunkProvider.getResolutionMax();

        if(Thread.interrupted())
            return;
        array.initNoiseMap(resolutionMin);
        array.generateNormalMap();
        array.updateImage(null);

        for (int i = resolutionMin + 1; i < resolutionMax + 4; i++) {
            array.increaseResolution((float)Math.pow(2, i));
            array.updateImage(null);
            array.generateNormalMap();
            array.convertData();

        }
        File outputfile = new File("image.png");
        try {
            ImageIO.write(array.bi, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}