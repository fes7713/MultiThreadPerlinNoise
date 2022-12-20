import java.awt.*;

interface NoiseChunkInterface {
    int getChunkX();
    int getChunkY();

    void setChunkShiftX(int chunkShiftX);
    void setChunkShiftY(int chunkShiftY);

    int getPixelShiftX();
    void setPixelShiftX(int pixelShiftX);
    int getPixelShiftY();
    void setPixelShiftY(int pixelShiftY);

    void updateChunk(PaintInterface pi);
    void updateImage(PaintInterface pi);
    void drawImage(Graphics2D g2d);

    String getName();
}
