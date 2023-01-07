package Noise.Array;

import Noise.ColorProvider;
import Noise.FastNoise;
import Noise.PaintInterface;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class PerlinNoiseArray implements PerlinNoiseArrayInterface{
    private final ColorProvider colorProvider;

    private float[][] noiseMap;
    private float[][] normalMap;
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

    private static float NOISE_COEFFICIENT = 4F;
    private static float NOISE_SHIFT = 0;
    private static float NORMAL_COEFFICIENT = 0.03F;
    private static float NORMAL_SHIFT = 125;

    private static float MASK_SIZE = 20;
    private static float MASK_SHADOW = 2F;

    private static float LIGHTING_ANGLE = 0;
    private static float LIGHTING_STRENGTH = 1;

    private static float LIGHTING_X = LIGHTING_STRENGTH * (float)Math.cos(Math.toRadians(LIGHTING_ANGLE));
    private static float LIGHTING_Y = LIGHTING_STRENGTH * (float)Math.sin(Math.toRadians(LIGHTING_ANGLE));
    private static float LIGHTING_Z = -1;

    public PerlinNoiseArray(ColorProvider colorProvider, FastNoise fn, float left, float top, int width, int height, float zoom, float centerX, float centerY){
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
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        generateFallOffMap();
    }

    public void initNoiseMap()
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                noiseMap[i][j] = fn.GetNoise(i  * zoom + left, j  * zoom + top);
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
        Vector3f light = new Vector3f(LIGHTING_X, LIGHTING_Y, LIGHTING_Z);
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
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                fallOffMap[i][j] = (float)Math.exp(- (
                        (i  * zoom + left - centerX) * (i  * zoom + left - centerX)
                                +
                                (j * zoom + top - centerY) * (j * zoom + top - centerY))
                        / MASK_SIZE / 1000000F);
            }
        }
    }

    public float convertNoise(float noise)
    {
//        return 1 - (float)Math.pow(2.75, -(noise + 0.75) * (noise + 0.75));
//        return (int)(Math.atan(100 * noise / 67) * 80) + 127;
//        return (float)(Math.atan( (noise - NOISE_SHIFT) * NOISE_COEFFICIENT) / Math.PI + 0.5);
        return (float)(1 / (1 + Math.exp(-NOISE_COEFFICIENT * (noise - NOISE_SHIFT))));
    }

    public float convertNormal(float normal)
    {
//        float t = 2.5F;
//        int p = 52;
//        int s = 32;
//        return (int)(Math.atan((normal - t * p) / s) * t * s + t * p);
//        return (int)(Math.atan((normal - 125) / 32F) * 80 + 125);
//        return (float)(Math.atan(normal) / Math.PI + 0.5);
//        return (float)(Math.atan( (normal - NORMAL_SHIFT) * NORMAL_COEFFICIENT) / Math.PI + 0.5);
        return (float)(1 / (1 + Math.exp(-NORMAL_COEFFICIENT * (normal - NORMAL_SHIFT))));
    }

    public void updateImage(PaintInterface pi)
    {
        int length = colorProvider.colors.length - 1;
        for(int i = 0; i < width - 1; i++)
        {
            for(int j = 0; j < height - 1; j++)
            {
                bi.setRGB(i, j, colorProvider
                        .colors[
                        (int)(convertNormal(normalMap[i][j])  * fallOffMap[i][j] * length)
                        ][
                        (int)(convertNoise(noiseMap[i][j])  * Math.pow(fallOffMap[i][j], MASK_SHADOW) * length)
                        ]);
            }
        }

        for(int i = 0; i < width; i++)
        {
            bi.setRGB(i, height - 1, colorProvider.colors[colorProvider.colors.length / 2][(int)(convertNoise(noiseMap[i][height - 1]) * colorProvider.colors.length)]);
        }

        for(int i = 0; i < height; i++)
        {
            bi.setRGB(width - 1, i, colorProvider.colors[colorProvider.colors.length / 2][(int)(convertNoise(noiseMap[width - 1][i]) * colorProvider.colors.length)]);
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

    protected static float getNoiseCoefficient(){
        return NOISE_COEFFICIENT;
    }

    protected static void setNoiseCoefficient(float coefficient)
    {
        NOISE_COEFFICIENT = coefficient;
    }

    protected static float getNoiseShift(){
        return NOISE_SHIFT;
    }

    protected static void setNoiseShift(float shift)
    {
        NOISE_SHIFT = shift;
    }

    protected static float getNormalCoefficient(){
        return NORMAL_COEFFICIENT;
    }

    protected static void setNormalCoefficient(float coefficient)
    {
        NORMAL_COEFFICIENT = coefficient;
    }

    protected static float getNormalShift(){
        return NORMAL_SHIFT;
    }

    protected static void setNormalShift(float shift)
    {
        NORMAL_SHIFT = shift;
    }

    public static float getMaskSize()
    {
        return MASK_SIZE;
    }

    public static void setMaskSize(float maskSize)
    {
        MASK_SIZE = maskSize;
    }

    public static float getMaskShadow()
    {
        return MASK_SHADOW;
    }

    public static void setMaskShadow(float maskShadow)
    {
        MASK_SHADOW = maskShadow;
    }

    public static float getLightingAngle() {
        return LIGHTING_ANGLE;
    }

    public static void setLightingAngle(float lightingAngle) {
        LIGHTING_ANGLE = lightingAngle;
        LIGHTING_X = LIGHTING_STRENGTH * (float)Math.cos(Math.toRadians(LIGHTING_ANGLE));
        LIGHTING_Y = LIGHTING_STRENGTH * (float)Math.sin(Math.toRadians(LIGHTING_ANGLE));
    }

    public static float getLightingStrength() {
        return LIGHTING_STRENGTH;
    }

    public static void setLightingStrength(float lightingStrength) {
        LIGHTING_STRENGTH = lightingStrength;
        LIGHTING_X = LIGHTING_STRENGTH * (float)Math.cos(Math.toRadians(LIGHTING_ANGLE));
        LIGHTING_Y = LIGHTING_STRENGTH * (float)Math.sin(Math.toRadians(LIGHTING_ANGLE));
    }

    public static float getLightingZ() {
        return LIGHTING_Z;
    }

    public static void setLightingZ(float lightingZ) {
        LIGHTING_Z = lightingZ;
    }
}