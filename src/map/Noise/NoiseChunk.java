package map.Noise;

import map.Noise.Array.PerlinNoiseArray;

import java.awt.*;

public class NoiseChunk implements NoiseChunkInterface{
    private int chunkX;
    private int chunkY;

    private final int width;
    private final int height;

    private final int arrayWidth;
    private final int arrayHeight;

    private int chunkShiftX;
    private int chunkShiftY;
    private int pixelShiftX;
    private int pixelShiftY;

    private final ChunkProvider chunkProvider;
    private final PerlinNoiseArray array;
    private Thread thread;

    public NoiseChunk(ChunkProvider chunkProvider, ColorProvider colorProvider, FastNoise fn, int chunkX, int chunkY, int width, int height, float zoom, float centerX, float centerY, int arrayWidth, int arrayHeight) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        this.width = width;
        this.height = height;
        this. arrayWidth = arrayWidth;
        this.arrayHeight = arrayHeight;

        thread = new Thread();

        chunkShiftX = chunkShiftY = 0;
        pixelShiftX = pixelShiftY = 0;

        this.chunkProvider = chunkProvider;
        array = new PerlinNoiseArray(chunkProvider, colorProvider, fn, chunkX * arrayWidth * zoom, chunkY * arrayHeight * zoom, arrayWidth, arrayHeight, zoom, centerX, centerY);
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
        array.reuse(chunkX * arrayWidth * zoom, chunkY * arrayHeight * zoom, zoom);
    }

    @Override
    public void setCenter(float centerX, float centerY) {
        array.setCenter(centerX, centerY);
    }

    @Override
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

                int resolutionMin = chunkProvider.getResolutionMin();
                int resolutionMax = chunkProvider.getResolutionMax();

                if(Thread.interrupted())
                    return;
//                array.initNoiseMap(resolutionMin);
                array.clearNoiseMap();
                if(Thread.interrupted())
                    return;
                array.generateNormalMap();
                if(Thread.interrupted())
                    return;
                array.updateImage(pi);
                if(Thread.interrupted())
                    return;
                Thread.yield();

                for (int i = resolutionMin + 1; i < resolutionMax; i++) {
                    if(Thread.interrupted())
                        return;
                    array.increaseResolution((float)Math.pow(2, i));
                    if(Thread.interrupted())
                        return;
                    array.updateImage(pi);
                    if(Thread.interrupted())
                        return;
                    Thread.yield();
                    array.generateNormalMap();
                }
                array.updateImage(pi);
            }
        };
        thread.start();
    }

    @Override
    public void updateLighting(PaintInterface pi)
    {
        Thread thread = new Thread(() -> {
            array.generateNormalMap();
            if(pi != null)
                pi.paint();
        });
        thread.start();
    }

    @Override
    public void updateImage(PaintInterface pi)
    {
        array.updateImage(pi);
    }

    @Override
    public void variableChanged() {
//        array.convertData();
    }

    @Override
    public void drawImage(Graphics2D g2d)
    {
        g2d.drawImage(array.getImage(),
                (chunkX + chunkShiftX) * width + pixelShiftX,
                (chunkY + chunkShiftY) * height + pixelShiftY,
                width,
                height, null
        );
    }

    @Override
    public String toString()
    {
        return "Chunk" + chunkX + "-" + chunkY;
    }
}