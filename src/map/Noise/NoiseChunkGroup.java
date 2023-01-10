package map.Noise;

import java.awt.*;
import java.lang.Math;

public class NoiseChunkGroup implements NoiseChunkInterface, NoiseChunkGroupInterface{
    private long key;
    private final String name;
    private final NoiseChunkInterface[][] chunkTable;

    private final int tableWidth;
    private final int tableHeight;

    private final int canvasWidth;
    private final int canvasHeight;

    private final ChunkProvider provider;

    public NoiseChunkGroup(ChunkProvider provider, String name, int canvasWidth, int canvasHeight, int tableWidth, int tableHeight) {
        this.name = name;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;

        chunkTable = new NoiseChunk[tableWidth][tableHeight];

        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        this.provider = provider;

        System.err.printf("%-20s Chunks are not loaded\n", "[" + name + "]");
    }

    public void loadChunks(int chunkX, int chunkY, boolean paintUpdate)
    {
        key = NoiseChunkInterface.getChunkKey(chunkX, chunkY);
        for (int i = 0; i < tableWidth; i++)
        {
            for (int j = 0; j < tableHeight; j++)
            {
                chunkTable[i][j] = provider.requestNoiseChunk(i + chunkX, j + chunkY, paintUpdate);
            }
        }
    }

    public String getName()
    {
        return name;
    }

    @Override
    public long getChunkKey() {
        return key;
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

    @Override
    public void stopChunk() {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].stopChunk();
            }
        }
    }

    @Override
    public void reuseChunk(int chunkX, int chunkY, float zoom){
        key = NoiseChunkInterface.getChunkKey(chunkX, chunkY);
        System.err.println("Dont come here");
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].reuseChunk(chunkX, chunkY, zoom);
            }
        }
    }

    @Override
    public void setCenter(float centerX, float centerY) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setCenter(centerX, centerY);
            }
        }
    }

    @Override
    public void updateLighting(PaintInterface pi) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].updateLighting(pi);
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
    public void variableChanged() {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].variableChanged();
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