import java.awt.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class NoiseChunk implements NoiseChunkInterface{
    private final String name;

    private int chunkX;
    private int chunkY;

    private int left;
    private int top;
    private int width;
    private int height;

    FastNoise fn;
    PerlinNoiseArray array;
    Thread thread;
    ReentrantLock lock;
    Semaphore semaphore;


    public NoiseChunk(String name, FastNoise fn, int chunkX, int chunkY, int width, int height, Semaphore semaphore) {
        this.name = name;
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

    public NoiseChunk(FastNoise fn, int chunkX, int chunkY, int width, int height, Semaphore semaphore) {
        this("Default", fn, chunkX, chunkY, width, height,semaphore);
    }

    public String getName()
    {
        return name;
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

    public synchronized void setWidth(int width) {
        this.width = width;
        array.setWidth(width);
        array.setLeft(chunkX * width);

    }

    public int getHeight() {
        return height;
    }

    public synchronized void setHeight(int height) {
        this.height = height;
        array.setHeight(height);
        array.setTop(chunkY * height);
    }

    @Override
    public synchronized void setDimension(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void updateChunk(PaintInterface pi)
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
                lock.lock();

                array.initNoiseMap();
                array.updateImage(pi);

                for (int i = 1; i < 8; i++) {
                    if(Thread.interrupted())
                    {
                        semaphore.release();
                        return;
                    }
                    array.increaseResolution((int)Math.pow(2, i));
                    array.updateImage(pi);
                }
//                System.out.println("Noise updated");
                semaphore.release();
                lock.unlock();
            }
        };
        thread.start();
    }

    public void updateImage(PaintInterface pi)
    {
        lock.lock();
        array.updateImage(pi);
        lock.unlock();
    }

    public void drawImage(Graphics2D g2d)
    {
        g2d.drawImage(array.getImage(), chunkX * width + left, chunkY * height + top, null);
    }

    @Override
    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
        array.setLeft(chunkX * width);
    }

    @Override
    public void setChunkY(int chunkY) {
        this.chunkY = chunkY;
        array.setTop(chunkY * height);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
