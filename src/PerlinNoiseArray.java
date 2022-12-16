import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.beans.beancontext.BeanContext;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PerlinNoiseArray {
    private float[][] noiseMap;
    private int height;
    private int width;

    FastNoise fn;
    BufferedImage bi;

    public PerlinNoiseArray(FastNoise fn, int width, int height){
        this.fn = fn;
        this.width = width;
        this.height = height;
        noiseMap = new float[width][height];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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

    private void dimensionChanged()
    {
        noiseMap = new float[width][height];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void updateNoiseMap()
    {
        float max = 0;
        float min = 0;
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                noiseMap[i][j] = fn.GetNoise(i, j);
                if(noiseMap[i][j] > max)
                    max = noiseMap[i][j];
                if(noiseMap[i][j] < min)
                    min  = noiseMap[i][j];
            }
        }


        System.out.println("Max" + max);
        System.out.println("Min" + min);

        float range = max - min;

        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {

                bi.setRGB(i, j, getIntFromColor(
                        (noiseMap[i][j] - min) / range,
                        (noiseMap[i][j] - min) / range,
                        (noiseMap[i][j] - min) / range));
            }
        }
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public int getIntFromColor(float Red, float Green, float Blue){
        int R = Math.round(255 * Red);
        int G = Math.round(255 * Green);
        int B = Math.round(255 * Blue);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return 0xFF000000 | R | G | B;
    }

    public void saveMapImage()
    {
        try {
            File outputfile = new File("saved.png");
            ImageIO.write(bi, "png", outputfile);
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
        Color color = new Color(254, 254, 128);
        byte[] bytes = ByteBuffer.allocate(4).putInt(color.getRGB()).array();

        System.out.println(Arrays.toString(bytes));
    }
}
