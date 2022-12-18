import java.awt.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
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
    Semaphore semaphore;

    public NoiseChunk(FastNoise fn, int chunkX, int chunkY, float left, float top, int width, int height, Semaphore semaphore) {
        this.fn = fn;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        thread = new Thread();
        lock = new ReentrantLock();
        this.semaphore = semaphore;

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
                    array.increaseResolution((int)Math.pow(2, i));
                    nri.noiseRangeUpdate(getNoiseMax(), getNoiseMin());
                    array.updateImage(pi);
                }
                System.out.println("Noise updated");
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
