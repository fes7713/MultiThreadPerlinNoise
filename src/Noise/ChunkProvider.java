package Noise;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ChunkProvider {
    private static final ChunkProvider provider = new ChunkProvider();

    private final FastNoise fn;
    private final Map<Long, NoiseChunkInterface> loadedNoiseMap;

    private int chunkWidth;
    private int chunkHeight;
    private float zoom;

    PaintInterface pi;
    private ChunkProvider()
    {
        zoom = 1;
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
        long key = (((long)col) << 32) | (row & 0xffffffffL);

        if(loadedNoiseMap.containsKey(key))
        {
            return loadedNoiseMap.get(key);
        }
        else{
            NoiseChunkInterface noiseChunk = new NoiseChunk("Chunk" + col + "-" + row, fn, col, row, chunkWidth, chunkHeight, zoom, semaphore);
            if(paintUpdate)
            {
                if(pi == null)
                    throw new RuntimeException("Need paint interface to be set before this method call");
                noiseChunk.updateChunk(pi);
            }

            else
                noiseChunk.updateChunk(null);
            loadedNoiseMap.put(key, noiseChunk);
            return noiseChunk;
        }
    }

    public void dimensionChanged(int chunkWidth, int chunkHeight)
    {
        loadedNoiseMap.clear();
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
    }

    public void zoomChanged(float zoom)
    {
        loadedNoiseMap.clear();
        this.zoom = zoom;
    }
}
