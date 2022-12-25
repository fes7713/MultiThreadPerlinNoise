package Noise;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

    public static void main(String[] args)
    {
//        Math.
    }
}
