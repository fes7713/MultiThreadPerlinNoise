import java.awt.*;
import java.awt.image.BufferedImage;

public class NoiseChunk implements NoiseChunkInterface{
    private final int chunkX;
    private final int chunkY;

    private float left;
    private float top;
    private int width;
    private int height;

    FastNoise fn;
    PerlinNoiseArray array;

    public NoiseChunk(FastNoise fn, int chunkX, int chunkY, float left, float top, int width, int height) {
        this.fn = fn;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        array = new PerlinNoiseArray(fn, chunkX * width + left, chunkY * height + top, width, height);
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        array.setLeft(chunkX * width + left);
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        array.setTop(chunkY * height + top);
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        array.setWidth(width);
        array.setLeft(chunkX * width + left);
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        array.setWidth(height);
        array.setLeft(chunkY * height + top);
        this.height = height;
    }

    public void updateChunk()
    {
        array.updateNoiseMap();
    }

    public void drawImage(Graphics2D g2d)
    {
        g2d.drawImage(array.getImage(), chunkX * width , chunkY * height, null);
    }
}
