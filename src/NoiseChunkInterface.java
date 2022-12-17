import java.awt.*;

interface NoiseChunkInterface {
    void setLeft(float left);
    void setTop(float top);
    float getLeft();
    float getTop();
    void setDimension(int width, int height);
    void updateChunk(PaintInterface pi, NoiseRangeInterface nri);
    void drawImage(Graphics2D g2d);
    float getNoiseMax();
    float getNoiseMin();
    void setNoiseRange(float max, float min);
}
