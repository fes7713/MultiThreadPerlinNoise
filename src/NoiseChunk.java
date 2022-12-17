import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

public class NoiseChunk implements NoiseChunkInterface{
    private final int chunkX;
    private final int chunkY;

    private float left;
    private float top;
    private int width;
    private int height;

    FastNoise fn;
    PerlinNoiseArray array;
    Thread thread;

    ReentrantLock lock;

    public NoiseChunk(FastNoise fn, int chunkX, int chunkY, float left, float top, int width, int height) {
        this.fn = fn;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        array = new PerlinNoiseArray(fn, chunkX * width + left, chunkY * height + top, width, height);
        thread = new Thread();
        lock = new ReentrantLock();
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
        this.width = width;
        array.setWidth(width);
        array.setLeft(chunkX * width + left);

    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        array.setHeight(height);
        array.setTop(chunkY * height + top);
    }

    @Override
    public void setDimension(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void updateChunk(PaintInterface pi, NoiseRangeInterface nri)
    {
        if(thread.isAlive())
        {
//            System.out.println("Active thread");
            thread.interrupt();
        }

        thread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                array.initNoiseMap();
                nri.noiseRangeUpdate(getNoiseMax(), getNoiseMin());
                array.updateImage(pi);

                for (int i = 1; i < 8; i++) {
                    array.increaseResolution((int)Math.pow(2, i));
                    nri.noiseRangeUpdate(getNoiseMax(), getNoiseMin());
                    array.updateImage(pi);
                }
            }
        };
        thread.start();
    }

    public void drawImage(Graphics2D g2d)
    {
        g2d.drawImage(array.getImage(), chunkX * width, chunkY * height, null);
    }

    @Override
    public float getNoiseMax() {
        return array.getNoiseMax();
    }

    @Override
    public float getNoiseMin() {
        return array.getNoiseMin();
    }

    @Override
    public void setNoiseRange(float max, float min) {
        array.setNoiseRange(max, min);
    }
}
