package Noise;

import java.awt.*;

interface NoiseChunkInterface {
    void setChunkShiftX(int chunkShiftX);
    void setChunkShiftY(int chunkShiftY);

    void setPixelShiftX(int pixelShiftX);
    void setPixelShiftY(int pixelShiftY);

    void updateChunk(PaintInterface pi);
    void stopChunk();
    void updateImage(PaintInterface pi);
    void drawImage(Graphics2D g2d);

    String getName();
}
