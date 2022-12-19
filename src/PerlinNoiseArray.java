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

    private float noiseMax;
    private float noiseMin;
    private float noiseRange;

    FastNoise fn;
    BufferedImage bi;

    public PerlinNoiseArray(FastNoise fn, int width, int height){
        this(fn, 0, 0, width, height);
    }

    public PerlinNoiseArray(FastNoise fn, float left, float top, int width, int height){
        this.fn = fn;
        this.left = left;
        this.top = top;
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

    public float getNoiseMax() {
        return noiseMax;
    }

    public float getNoiseMin() {
        return noiseMin;
    }

    private void dimensionChanged()
    {
        noiseMap = new float[width][height];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void initNoiseMap()
    {
        noiseMax = noiseMin = 0;
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                noiseMap[i][j] = fn.GetNoise(i + left, j + top);
                if(noiseMap[i][j] > noiseMax)
                    noiseMax = noiseMap[i][j];
                if(noiseMap[i][j] < noiseMin)
                    noiseMin  = noiseMap[i][j];
            }
        }

        noiseRange = noiseMax - noiseMin;
    }

    public void increaseResolution(int resolution)
    {
        noiseMax = noiseMin = 0;
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                noiseMap[i][j] += fn.GetNoise((i  + left) * resolution, (j + top) * resolution ) / resolution;
                if(noiseMap[i][j] > noiseMax)
                    noiseMax = noiseMap[i][j];
                if(noiseMap[i][j] < noiseMin)
                    noiseMin  = noiseMap[i][j];
            }
        }

        noiseRange = noiseMax - noiseMin;
    }

    public float convertNoise(float noise)
    {
//        return 1 - 0.5F / (noise + 1.25F);
        return 1 - (float)Math.pow(2.75, -(noise + 0.75) * (noise + 0.75));
    }

    public void updateImage(PaintInterface pi)
    {
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
//                bi.setRGB(i, j, getIntFromColor(
//                        (noiseMap[i][j] - noiseMin) / noiseRange,
//                        (noiseMap[i][j] - noiseMin) / noiseRange,
//                        (noiseMap[i][j] - noiseMin) / noiseRange));
                bi.setRGB(i, j, getIntFromColor(
                            convertNoise(noiseMap[i][j]),
                            convertNoise(noiseMap[i][j]),
                            convertNoise(noiseMap[i][j])
                        )
                );
            }
        }

        if(pi != null)
            pi.paint();
    }

    public void setNoiseRange(float max, float min)
    {
        if(noiseMax < max)
        {
            noiseMax = max;
            noiseRange = noiseMax - noiseMin;
        }
        else if(noiseMin > min)
        {
            noiseMin = min;
            noiseRange = noiseMax - noiseMin;
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
}
