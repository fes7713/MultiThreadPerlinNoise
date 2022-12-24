package Noise;

import java.awt.*;
import java.lang.Math;
import java.util.concurrent.Semaphore;

public class NoiseChunkGroup implements NoiseChunkInterface, NoiseChunkGroupInterface{
    private final String name;
    private final NoiseChunkInterface[][] chunkTable;

    private final int tableWidth;
    private final int tableHeight;

    private final int canvasWidth;
    private final int canvasHeight;

    private final Semaphore semaphore;

    private final ChunkProvider provider;

    public NoiseChunkGroup(String name, int canvasWidth, int canvasHeight, int tableWidth, int tableHeight, Semaphore semaphore) {
        this.name = name;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;

        chunkTable = new NoiseChunk[tableWidth][tableHeight];

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        this.semaphore = semaphore;
        provider = ChunkProvider.getInstance();

        System.err.printf("%-20s Chunks are not loaded\n", "[" + name + "]");
    }

    public NoiseChunkGroup(String name,  int canvasWidth, int canvasHeight, int tableWidth, int tableHeight) {
        this(name, canvasWidth, canvasHeight, tableWidth, tableHeight, new Semaphore(tableWidth * tableHeight));
    }

    public void loadChunks(int chunkX, int chunkY, boolean paintUpdate)
    {
        for (int i = 0; i < tableWidth; i++)
        {
            for (int j = 0; j < tableHeight; j++)
            {
                chunkTable[i][j] = provider.requestNoiseChunk(i + chunkX, j + chunkY, paintUpdate, semaphore);
            }
        }
    }

    public String getName()
    {
        return name;
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
    public void setPixelShiftX(int pixelShiftX) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setPixelShiftX(pixelShiftX);
            }
        }
    }

    @Override
    public void setPixelShiftY(int pixelShiftY) {
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
