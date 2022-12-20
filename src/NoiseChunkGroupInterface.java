public interface NoiseChunkGroupInterface {
    NoiseChunkInterface[][] pushLeft(NoiseChunkGroup ncg, int length);
    NoiseChunkInterface[][] pushLeft(NoiseChunkInterface[][] pushedChunks);
    NoiseChunkInterface[][] pushTop(NoiseChunkGroup ncg, int length);
    NoiseChunkInterface[][] pushTop(NoiseChunkInterface[][] pushedChunks);
    NoiseChunkInterface[][] pushRight(NoiseChunkGroup ncg, int length);
    NoiseChunkInterface[][] pushRight(NoiseChunkInterface[][] pushedChunks);
    NoiseChunkInterface[][] pushBottom(NoiseChunkGroup ncg, int length);
    NoiseChunkInterface[][] pushBottom(NoiseChunkInterface[][] pushedChunks);
    int getChunkWidth();
    int getChunkHeight();
}
