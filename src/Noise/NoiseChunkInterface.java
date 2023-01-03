package Noise;

import java.awt.*;

interface NoiseChunkInterface {
    long getChunkKey();
    void setChunkShiftX(int chunkShiftX);
    void setChunkShiftY(int chunkShiftY);

    void setPixelShiftX(int pixelShiftX);
    void setPixelShiftY(int pixelShiftY);

    void updateChunk(PaintInterface pi);
    void stopChunk();
    void reuseChunk(int chunkX, int chunkY, float zoom);
    void setCenter(float centerX, float centerY);

    void updateImage(PaintInterface pi);
    void drawImage(Graphics2D g2d);

    String getName();

    static long getChunkKey(int col, int row)
    {
        return (((long)col) << 32) | (row & 0xffffffffL);
    }
}
