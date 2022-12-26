package Noise;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.vecmath.Vector3f;


public class PerlinNoiseArray {
    private float[][] noiseMap;

    private float left;
    private float top;
    private int height;
    private int width;

    private final FastNoise fn;
    private BufferedImage bi;
    private float zoom;

    public PerlinNoiseArray(FastNoise fn, float left, float top, int width, int height, float zoom){
        this.zoom = zoom;
        this.fn = fn;
        this.left = left * zoom;
        this.top = top * zoom;
        this.width = width;
        this.height = height;

        noiseMap = new float[width][height];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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
    }

    private void dimensionChanged()
    {
        noiseMap = new float[width][height];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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

    public void increaseResolution(int resolution)
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                noiseMap[i][j] += fn.GetNoise((i * zoom  + left) * resolution, (j * zoom + top) * resolution ) / resolution;
            }
        }
    }

    public float convertNoise(float noise)
    {
        return 1 - (float)Math.pow(2.75, -(noise + 0.75) * (noise + 0.75));
    }

    public void updateImage(PaintInterface pi)
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                bi.setRGB(i, j, ColorProvider.COLORS[(int)(convertNoise(noiseMap[i][j]) * ColorProvider.COLORS.length)]);
            }
        }

        for(int i = 0; i < width - 1; i++)
        {
            for(int j = 0; j < height - 1; j++)
            {
                Color color = valueOf(bi.getRGB(i, j));
                float light = 1000 * (1 +lightIntensity(
                                zoom, 0, convertNoise(noiseMap[i + 1][j]) - convertNoise(noiseMap[i][j]),
                                0, zoom, convertNoise(noiseMap[i][j + 1]) - convertNoise(noiseMap[i][j]),
                                                new Vector3f(0, -1, -1)));


                if(light > 6)
                    color = color.brighter();
                if(light > 4)
                    color = color.brighter();



                if(light < -1)
                    color = color.darker();
                if(light < -2)
                    color = color.darker();
                if(light < -3)
                    color = color.darker();
                bi.setRGB(i, j, color.getRGB());
            }
        }

        if(pi != null)
            pi.paint();
    }

    public static Color valueOf(int color) {
        float r = ((color >> 16) & 0xff) / 255.0f;
        float g = ((color >>  8) & 0xff) / 255.0f;
        float b = ((color      ) & 0xff) / 255.0f;
        return new Color(r, g, b);
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

    public static float lightIntensityFromPoints(float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, Vector3f light) {
        return lightIntensity(bx - ax, by - ay, bz - az, cx - ax, cy - ay, cz - az, light);
    }
    public static float lightIntensity(float a1, float a2, float a3, float b1, float b2, float b3, Vector3f light)
    {
        float c1 = a2 * b3 - a3 * b2;
        float c2 = a3 * b1 - a1 * b3;
        float c3 = a1 * b2 - a2 * b1;

        return (c1 * light.x + c2 * light.y + c3 * light.z) / (float)Math.sqrt(c1 * c1 + c2 * c2 + c3 * c3);
    }

    public static void main(String[] args)
    {
        Vector3f v1 = new Vector3f(0, 0, 0);
        Vector3f v2 = new Vector3f(1, 0, 0.005F);
        Vector3f v3 = new Vector3f(0, 1, 0.01F);

        Vector3f light = new Vector3f(0, -1, -1);

        System.out.println(lightIntensity(v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, light));

        Vector3f v5 = new Vector3f();
        v5.cross(v2, v3);
        System.out.println(v5.dot(light));
        System.out.println(lightIntensityFromPoints(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, light));

        Color c = new Color(5, 98, 33);
        Color c1  = valueOf(c.getRGB());
        System.out.println(c1);
    }
}
