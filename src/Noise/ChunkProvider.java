package Noise;

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
        chunkWidth = chunkHeight = 200;
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
            NoiseChunkInterface noiseChunk = new NoiseChunk("Chunk" + col + "-" + row, fn, col, row, chunkWidth, chunkHeight, semaphore);

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

    public void dimensionChanged(int chunkWidth, int chunkHeight)
    {
        loadedNoiseMap.clear();
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
    }
}
