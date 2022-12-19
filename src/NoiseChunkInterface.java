import java.awt.*;

interface NoiseChunkInterface {
    void setLeft(int left);
    void setTop(int top);
    int getLeft();
    int getTop();
    void setDimension(int width, int height);
    void updateChunk(PaintInterface pi);
    void updateImage(PaintInterface pi);
    void drawImage(Graphics2D g2d);
    void setChunkX(int chunkX);
    void setChunkY(int chunkY);
    void setWidth(int width);
    void setHeight(int height);
    String getName();
}
