package Noise;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class PerlinNoiseArray {
    private float[][] noiseMap;
    private float[][] normalMap;

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
        // TODo
        normalMap = new float[width][height];

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
        normalMap = new float[width][height];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void initNoiseMap()
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
//                x′=xcosθ−ysinθ
//            y′=xsinθ+ycosθ
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

    public int convertNormal(float normal)
    {
//        float t = 2.5F;
//        int p = 52;
//        int s = 32;
//        return (int)(Math.atan((normal - t * p) / s) * t * s + t * p);
        return (int)(Math.atan((normal - 125) / 32F) * 80 + 125);
    }

    public void generateNormalMap()
    {
        Vector3f light = new Vector3f(0, -1, -1);
        for(int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
//                Vector3f v1 = new Vector3f(zoom, 0, noiseMap[i + 1][j] - noiseMap[i][j]);
//                Vector3f v2 = new Vector3f(0, zoom, noiseMap[i][j + 1] - noiseMap[i][j]);
//                Vector3f v3 = new Vector3f();
//                v3.cross(v1, v2);
//                System.out.println(v3.dot(new Vector3f(0, -1, -1)));
//                ((A1+1)*4000 + 125;
                float normal = lightIntensity(
                        zoom, 0, noiseMap[i + 1][j] - noiseMap[i][j],
                        0, zoom, noiseMap[i][j + 1] - noiseMap[i][j],
                        light);
                float normalized = (normal + 1) * 4096 + 125;

//                float normalized = ((normal+1)*1600 + 50)*3.14F;
                normalMap[i][j] = normalized;

//                normalMap[i][j] = v3.dot(new Vector3f(0, -1, -1));
            }
        }
    }

    public float convertNoise(float noise)
    {
//        return 1 - (float)Math.pow(2.75, -(noise + 0.75) * (noise + 0.75));
//        return (int)(Math.atan(100 * noise / 67) * 80) + 127;
        return (float)(Math.atan( noise * 4) / Math.PI + 0.5);
    }

    public void updateImage(PaintInterface pi)
    {
        for(int i = 0; i < width - 1; i++)
        {
            for(int j = 0; j < height - 1; j++)
            {
//                float light = 150 * (2 + lightIntensity(
//                        zoom, 0, convertNoise(noiseMap[i + 1][j]) - convertNoise(noiseMap[i][j]),
//                        0, zoom, convertNoise(noiseMap[i][j + 1]) - convertNoise(noiseMap[i][j]),
//                        new Vector3f(0, -1, -1)));

//                bi.setRGB(i, j, ColorProvider.COLORS[127][(int)(convertNoise(noiseMap[i][j]) * ColorProvider.COLORS.length)]);
                // TODO delete
                if(convertNoise(noiseMap[i][j]) == 255)
                    System.out.println(normalMap[i][j]);
                bi.setRGB(i, j, ColorProvider
                        .COLORS[
                                convertNormal(normalMap[i][j])
                        ][
                            (int)(convertNoise(noiseMap[i][j]) * ColorProvider.COLORS.length)
                        ]);
            }
        }

        for(int i = 0; i < width; i++)
        {
            bi.setRGB(i, height - 1, ColorProvider.COLORS[127][(int)(convertNoise(noiseMap[i][height - 1]) * ColorProvider.COLORS.length)]);
        }

        for(int i = 0; i < height; i++)
        {
            bi.setRGB(width - 1, i, ColorProvider.COLORS[127][(int)(convertNoise(noiseMap[width - 1][i]) * ColorProvider.COLORS.length)]);
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

    public static float lightIntensity(float a1, float a2, float a3, float b1, float b2, float b3, Vector3f light)
    {
        float c1 = a2 * b3 - a3 * b2;
        float c2 = b1 * a3 - b3 * a1;
        float c3 = a1 * b2 - a2 * b1;

        return (float)((c1 * light.x + c2 * light.y + c3 * light.z) / (Math.sqrt(c1 * c1 + c2 * c2 + c3 * c3)));
    }

    public static void main(String[] args)
    {
//        Vector3f v1 = new Vector3f(0, 0, 0);
//        Vector3f v2 = new Vector3f(1, 0, 0.005F);
//        Vector3f v3 = new Vector3f(0, 1, 0.01F);
//
//        Vector3f light = new Vector3f(0, -1, -1);
//
//        System.out.println(lightIntensity(v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, light));
//
//        Vector3f v5 = new Vector3f();
//        v5.cross(v2, v3);
//        System.out.println(v5.dot(light));
//        System.out.println(lightIntensityFromPoints(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, light));
//
//        Color c = new Color(5, 98, 33);
//        Color c1  = valueOf(c.getRGB());
//        System.out.println(c1);
//
//        System.out.println(Color.WHITE.getRGB());
        FastNoise fn = new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);
        PerlinNoiseArray noiseArray = new PerlinNoiseArray(fn, 0, 0, 4, 10000, 1);
//        noiseArray.initNoiseMap();

        for (int i = -2; i < 16; i++) {
            noiseArray.increaseResolution((float)Math.pow(2, i));
        }

        noiseArray.generateNormalMap();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < noiseArray.normalMap[0].length; i++) {
            sb.append(noiseArray.convertNormal(noiseArray.normalMap[0][i]));
            sb.append("\n");
        }

//        try {
//            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("normal7.csv"));
//            outputWriter.write(sb.toString());
//            outputWriter.flush();
//            outputWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        for(int i = 0; i < 255; i++)
            System.out.println(noiseArray.convertNormal(i));

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
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter("zoomres.csv"));
            outputWriter.write(sb1.toString());
            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
