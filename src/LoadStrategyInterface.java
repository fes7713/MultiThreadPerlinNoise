import Noise.NoiseChunkGroup;

public interface LoadStrategyInterface {
    void load(NoiseChunkGroup mainGroup, int chunkX, int chunkY);
}
