package map.Noise;

public interface NoiseChunkGroupInterface {
    void loadChunks(int chunkX, int chunkY, boolean paintUpdate);
    int getChunkWidth();
    int getChunkHeight();

    void setCanvasDimension(int width, int height);
}
