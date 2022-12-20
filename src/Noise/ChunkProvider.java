package Noise;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ChunkProvider {
    private static final ChunkProvider provider = new ChunkProvider();

    FastNoise fn;
    private final Map<Integer, Map<Integer, NoiseChunkInterface>> loadedNoiseMap;

    private int chunkWidth;
    private int chunkHeight;

    PaintInterface pi;
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
    public static ChunkProvider getInstance(PaintInterface pi){
        provider.setPaintInterface(pi);
        return provider;
    }


    public void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
    }

    public NoiseChunkInterface requestNoiseChunk(int col, int row, boolean paintUpdate, Semaphore semaphore)
    {
        if(loadedNoiseMap.containsKey(col) && loadedNoiseMap.get(col).containsKey(row))
        {
            return loadedNoiseMap.get(col).get(row);
        }
        else{
            NoiseChunkInterface noiseChunk = new NoiseChunk("Chunk" + col + "-" + row, fn, col, row, chunkWidth, chunkHeight, semaphore);
            if(paintUpdate)
            {
                if(pi == null)
                    throw new RuntimeException("Need paint interface to be set before this method call");
                noiseChunk.updateChunk(pi);
            }

            else
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

    public void dimensionChanged(int chunkWidth, int chunkHeight)
    {
        loadedNoiseMap.clear();
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
    }
}
