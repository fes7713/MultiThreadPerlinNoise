import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ChunkProvider {
    private static ChunkProvider provider = new ChunkProvider();

    FastNoise fn;
    private  Map<Integer, Map<Integer, NoiseChunkInterface>> loadedNoiseMap;

    private int chunkWidth;
    private int chunkHeight;


    private ChunkProvider()
    {
        loadedNoiseMap = new HashMap<>();
        fn = new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);
    }

    public static ChunkProvider getInstance(){
        return provider;
    }

    public NoiseChunkInterface requestNoiseChunk(int col, int row, Semaphore semaphore)
    {
        if(loadedNoiseMap.containsKey(col) && loadedNoiseMap.get(col).containsKey(row))
        {
            return loadedNoiseMap.get(col).get(row);
        }
        else{
            NoiseChunkInterface noiseChunk = new NoiseChunk(fn, col, row, chunkWidth, chunkHeight, semaphore);
            noiseChunk.updateChunk(null);
            if(loadedNoiseMap.containsKey(col)) {
                loadedNoiseMap.get(col).put(row, noiseChunk);
            }else{
                loadedNoiseMap.put(col,
                        new HashMap<>(){{
                            put(row, noiseChunk);
                        }
                        });
            }
            return noiseChunk;
        }
    }
}
