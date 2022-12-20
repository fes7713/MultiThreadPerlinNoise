package Noise;

import java.awt.*;
import java.lang.Math;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NoiseChunkGroup implements NoiseChunkInterface, NoiseChunkGroupInterface{
    private final String name;
    private NoiseChunkInterface[][] chunkTable;

    private int chunkX;
    private int chunkY;
    private final int chunkWidth;
    private final int chunkHeight;

//    private int chunkShiftX;
//    private int chunkShiftY;
    private int pixelShiftX;
    private int pixelShiftY;

    private final int tableWidth;
    private final int tableHeight;

    private final int canvasWidth;
    private final int canvasHeight;

    private final Semaphore semaphore;
    private final Lock lock;

    private final ChunkProvider provider;

    public NoiseChunkGroup(String name, int canvasWidth, int canvasHeight, int tableWidth, int tableHeight, Semaphore semaphore) {
        this.name = name;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;

        chunkTable = new NoiseChunk[tableWidth][tableHeight];

        chunkX = chunkY = 0;
//        chunkShiftX = chunkShiftY = 0;
//        pixelShiftX = pixelShiftY = 0;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        chunkWidth = getChunkWidth();
        chunkHeight = getChunkHeight();

        this.semaphore = semaphore;
        lock = new ReentrantLock();
        provider = ChunkProvider.getInstance();
        loadChunks(chunkX, chunkY, false);
    }

    public NoiseChunkGroup(String name,  int canvasWidth, int canvasHeight, int tableWidth, int tableHeight) {
        this(name, canvasWidth, canvasHeight, tableWidth, tableHeight, new Semaphore(tableWidth * tableHeight));
    }

    public NoiseChunkGroup(int canvasWidth, int canvasHeight, int tableWidth, int tableHeight) {
        this("Default", canvasWidth, canvasHeight, tableWidth, tableHeight);
    }

    public NoiseChunkGroup(int canvasWidth, int canvasHeight, int tableWidth, int tableHeight, Semaphore semaphore) {
        this("Default" ,canvasWidth, canvasHeight, tableWidth, tableHeight, semaphore);
    }

    public void loadChunks(int chunkX, int chunkY, boolean paintUpdate)
    {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        for (int i = 0; i < tableWidth; i++)
        {
            for (int j = 0; j < tableHeight; j++)
            {
                chunkTable[i][j] = provider.requestNoiseChunk(i + chunkX, j + chunkY, paintUpdate, semaphore);
            }
        }
    }

    public void loadChunks(int chunkX, int chunkY)
    {
        loadChunks(chunkX, chunkY, true);
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
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setChunkShiftX(chunkShiftX);
            }
        }
    }

    @Override
    public void setChunkShiftY(int chunkShiftY) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setChunkShiftY(chunkShiftY);
            }
        }
    }

    @Override
    public int getPixelShiftX() {
        return pixelShiftX;
    }

    @Override
    public void setPixelShiftX(int pixelShiftX) {
        this.pixelShiftX = pixelShiftX;
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setPixelShiftX(pixelShiftX);
            }
        }
    }

    @Override
    public int getPixelShiftY() {
        return pixelShiftY;
    }

    @Override
    public void setPixelShiftY(int pixelShiftY) {
        this.pixelShiftY = pixelShiftY;
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setPixelShiftY(pixelShiftY);
            }
        }
    }

    @Override
    public void updateChunk(PaintInterface pi) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].updateChunk(pi);
            }
        }

//        Runnable afterTasks = () -> {
//            try {
//                semaphore.acquire(tableHeight * tableWidth);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            updateImage(pi);
//            semaphore.release(tableHeight * tableWidth);
//        };
//        new Thread(afterTasks).start();
    }

    public void updateImage(PaintInterface pi)
    {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].updateImage(pi);
            }
        }
    }

    @Override
    public void drawImage(Graphics2D g2d) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].drawImage(g2d);
            }
        }
    }

    @Override
    public int getChunkWidth() {
        return (int)Math.ceil(canvasWidth / (double)tableWidth);

    }

    @Override
    public int getChunkHeight() {
        return (int)Math.ceil(canvasHeight / (double)tableHeight);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableWidth; i++) {
            sb.append("[");
            for (int j = 0; j < tableHeight; j++) {
                sb.append(chunkTable[i][j]);
                sb.append(", ");
            }
            sb.append("], \n");
        }
        return sb.toString();
    }
}
