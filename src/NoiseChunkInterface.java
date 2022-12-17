import java.awt.*;

interface NoiseChunkInterface {
    void setLeft(float left);
    void setTop(float top);
    float getLeft();
    float getTop();
    void updateChunk();
    void drawImage(Graphics2D g2d);
}
