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

    private final ChunkProvider chunkProvider;
    private final PerlinNoiseArray array;
    private Thread thread;

    public NoiseChunk(String name, ChunkProvider chunkProvider, ColorProvider colorProvider, FastNoise fn, int chunkX, int chunkY, int width, int height, float zoom, float centerX, float centerY) {
        this.name = name;
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        this.width = width;
        this.height = height;
        thread = new Thread();

        chunkShiftX = chunkShiftY = 0;
        pixelShiftX = pixelShiftY = 0;

        this.chunkProvider = chunkProvider;
        array = new PerlinNoiseArray(chunkProvider, colorProvider, fn, chunkX * width * zoom, chunkY * height * zoom, width, height, zoom, centerX, centerY);
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
        array.reuse(chunkX * width * zoom, chunkY * height * zoom, zoom);
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


//                for (int i = -2; i < 0; i++) {
//                    if(Thread.interrupted())
//                    {
//                        return;
//                    }
//
//                    array.increaseResolution((float)Math.pow(2, i));
//                    array.updateImage(pi);
//                    array.generateNormalMap();
//                    Thread.yield();
//                }
                int resolutionMin = chunkProvider.getResolutionMin();
                int resolutionMax = chunkProvider.getResolutionMax();

                array.initNoiseMap(resolutionMin);
                array.generateNormalMap();
                array.updateImage(pi);
                Thread.yield();

                for (int i = resolutionMin + 1; i < resolutionMax; i++) {
                    if(Thread.interrupted())
                    {
                        return;
                    }

                    array.increaseResolution((float)Math.pow(2, i));
                    array.updateImage(pi);
                    Thread.yield();
                    array.generateNormalMap();
                    array.convertData();

                }
                array.updateImage(pi);
            }
        };
        thread.start();
    }

    public void updateLighting(PaintInterface pi)
    {
        array.generateNormalMap();
        array.convertData();
        if(pi != null)
            pi.paint();
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