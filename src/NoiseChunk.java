import java.awt.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NoiseChunk implements NoiseChunkInterface{
    private final String name;

    private final int chunkX;
    private final int chunkY;
    private final int width;
    private final int height;

    private int chunkShiftX;
    private int chunkShiftY;
    private int pixelShiftX;
    private int pixelShiftY;


    FastNoise fn;
    PerlinNoiseArray array;
    Thread thread;
    Lock lock;
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

        chunkShiftX = chunkShiftY = 0;
        pixelShiftX = pixelShiftY = 0;

        array = new PerlinNoiseArray(fn, chunkX * width, chunkY * height, width, height);
    }

    public NoiseChunk(FastNoise fn, int chunkX, int chunkY, int width, int height, Semaphore semaphore) {
        this("Default", fn, chunkX, chunkY, width, height,semaphore);
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int getChunkX() {
        return chunkX;
    }

    @Override
    public int getChunkY() {
        return chunkY;
    }

    @Override
    public void setChunkShiftX(int chunkShiftX) {
        this.chunkShiftX = chunkShiftX;
    }

    @Override
    public void setChunkShiftY(int chunkShiftY) {
        this.chunkShiftY = chunkShiftY;
    }

    @Override
    public int getPixelShiftX() {
        return pixelShiftX;
    }

    @Override
    public void setPixelShiftX(int pixelShiftX) {
        this.pixelShiftX = pixelShiftX;
    }

    @Override
    public int getPixelShiftY() {
        return pixelShiftY;
    }

    @Override
    public void setPixelShiftY(int pixelShiftY) {
        this.pixelShiftY = pixelShiftY;
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
//        lock.lock();
        array.updateImage(pi);
//        lock.unlock();
    }

    public void drawImage(Graphics2D g2d)
    {
        g2d.drawImage(array.getImage(),
                (chunkX + chunkShiftX) * width + pixelShiftX,
                (chunkY + chunkShiftY) * height + pixelShiftY, null
        );
    }

    @Override
    public String toString()
    {
        return name;
    }
}
