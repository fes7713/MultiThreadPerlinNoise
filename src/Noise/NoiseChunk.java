package Noise;

import java.awt.*;
import java.util.concurrent.Semaphore;

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

    private final PerlinNoiseArray array;
    private Thread thread;


    public NoiseChunk(String name, FastNoise fn, int chunkX, int chunkY, int width, int height, float zoom) {
        this.name = name;
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        this.width = width;
        this.height = height;
        thread = new Thread();

        chunkShiftX = chunkShiftY = 0;
        pixelShiftX = pixelShiftY = 0;

        array = new PerlinNoiseArray(fn, chunkX * width, chunkY * height, width, height, zoom);
    }

    public String getName()
    {
        return name;
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
    public void setPixelShiftX(int pixelShiftX) {
        this.pixelShiftX = pixelShiftX;
    }

    @Override
    public void setPixelShiftY(int pixelShiftY) {
        this.pixelShiftY = pixelShiftY;
    }

    public void stopChunk()
    {
        thread.interrupt();
    }

    public void updateChunk(PaintInterface pi)
    {
        if(thread.isAlive())
        {
            thread.interrupt();
        }
        thread = new Thread()
        {
            @Override
            public void run() {
                super.run();

                array.initNoiseMap();
                array.updateImage(pi);
                Thread.yield();
                for (int i = 1; i < 8; i++) {
                    if(Thread.interrupted())
                    {
                        return;
                    }
                    array.increaseResolution((int)Math.pow(2, i));
                    array.updateImage(pi);
                    Thread.yield();
                }
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
        g2d.drawImage(array.getImage(),
                (chunkX + chunkShiftX) * width + pixelShiftX,
                (chunkY + chunkShiftY) * height + pixelShiftY, null
        );
        g2d.drawRect(
                (chunkX + chunkShiftX) * width + pixelShiftX,
                (chunkY + chunkShiftY) * height + pixelShiftY,
                width,
                height
        );
    }

    @Override
    public String toString()
    {
        return name;
    }
}
