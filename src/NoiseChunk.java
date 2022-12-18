import java.awt.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class NoiseChunk implements NoiseChunkInterface{
    private final int chunkX;
    private final int chunkY;

    private int left;
    private int top;
    private int width;
    private int height;

    FastNoise fn;
    PerlinNoiseArray array;
    Thread thread;
    ReentrantLock lock;
    Semaphore semaphore;



    public NoiseChunk(FastNoise fn, int chunkX, int chunkY, int width, int height, Semaphore semaphore) {
        this.fn = fn;
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        this.width = width;
        this.height = height;
        thread = new Thread();
        lock = new ReentrantLock();
        this.semaphore = semaphore;

        left = 0;
        top = 0;

        array = new PerlinNoiseArray(fn, chunkX * width, chunkY * height, width, height);
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        array.setWidth(width);
        array.setLeft(chunkX * width);

    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        array.setHeight(height);
        array.setTop(chunkY * height);
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
//        lock.lock();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread = new Thread()
        {
            @Override
            public void run() {
                super.run();
//                lock.lock();

                array.initNoiseMap();
                nri.noiseRangeUpdate(getNoiseMax(), getNoiseMin());
                array.updateImage(pi);

                for (int i = 1; i < 8; i++) {
                    if(Thread.interrupted())
                    {
                        semaphore.release();
                        return;
                    }
                    array.increaseResolution((int)Math.pow(2, i));
                    nri.noiseRangeUpdate(getNoiseMax(), getNoiseMin());
                    array.updateImage(pi);
                }
//                System.out.println("Noise updated");
                semaphore.release();
//                lock.unlock();
            }
        };
        thread.start();
    }

    public void updateImage(PaintInterface pi)
    {
        array.updateImage(pi);
    }

    public void drawImage(Graphics2D g2d)
    {
        g2d.drawImage(array.getImage(), chunkX * width + left, chunkY * height + top, null);
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
