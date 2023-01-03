package Noise;

import Noise.Array.PerlinNoiseArray;

import java.awt.*;

public class NoiseChunk implements NoiseChunkInterface{
    private final String name;

    private int chunkX;
    private int chunkY;

    private final int width;
    private final int height;

    private int chunkShiftX;
    private int chunkShiftY;
    private int pixelShiftX;
    private int pixelShiftY;

    private final PerlinNoiseArray array;
    private Thread thread;

    public NoiseChunk(String name, FastNoise fn, int chunkX, int chunkY, int width, int height, float zoom, float centerX, float centerY) {
        this.name = name;
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        this.width = width;
        this.height = height;
        thread = new Thread();

        chunkShiftX = chunkShiftY = 0;
        pixelShiftX = pixelShiftY = 0;

        array = new PerlinNoiseArray(fn, chunkX * width, chunkY * height, width, height, zoom, centerX, centerY);
    }

    public String getName()
    {
        return name;
    }

    @Override
    public long getChunkKey() {
        return NoiseChunkInterface.getChunkKey(chunkX, chunkY);
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

    @Override
    public void reuseChunk(int chunkX, int chunkY, float zoom) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        array.reuse(chunkX * width, chunkY * height, zoom);
    }

    @Override
    public void setCenter(float centerX, float centerY) {
        array.setCenter(centerX, centerY);
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
                Thread.yield();
                for (int i = -3; i < 0; i++) {
                    if(Thread.interrupted())
                    {
                        return;
                    }

                    array.increaseResolution((float)Math.pow(2, i));
                    array.updateImage(pi);
                    array.generateNormalMap();
                    Thread.yield();
                }
                for (int i = 1; i < 10; i++) {
                    if(Thread.interrupted())
                    {
                        return;
                    }

                    array.increaseResolution((float)Math.pow(2, i));
                    array.updateImage(pi);

                    array.generateNormalMap();
                    Thread.yield();
                }
                array.updateImage(pi);
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
    }

    @Override
    public String toString()
    {
        return name;
    }
}
