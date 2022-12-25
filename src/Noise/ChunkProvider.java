package Noise;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ChunkProvider {
    private class ReusableChunkKeeper
    {
        private final Map<Long, NoiseChunkInterface> chunkMap;
        private final Stack<NoiseChunkInterface> chunkStack;

        public ReusableChunkKeeper() {
            chunkMap = new HashMap<>();
            chunkStack = new Stack<>();
        }

        public void keepAllChunks(Map<Long, NoiseChunkInterface> map)
        {
            for(Map.Entry<Long, NoiseChunkInterface> entry: map.entrySet())
            {
                chunkMap.put(entry.getKey(), entry.getValue());
                chunkStack.add(entry.getValue());
            }
            if(chunkStack.size() != chunkMap.size())
                System.out.println("Errorrrr");
        }

        public void keepChunk(int col, int row, NoiseChunkInterface chunk)
        {
            long key = NoiseChunkInterface.getChunkKey(col, row);

            if(chunkStack.contains(chunk))
                throw new RuntimeException("Cannot keep the duplicate chunk");
            chunkMap.put(key, chunk);
            chunkStack.push(chunk);
            if(chunkStack.size() != chunkMap.size())
                System.out.println("Errorrrr");
        }

        public NoiseChunkInterface reuseChunk(int col, int row, float zoom)
        {
            long key = NoiseChunkInterface.getChunkKey(col, row);

            if(chunkStack.isEmpty())
            {
                return null;
            }

            NoiseChunkInterface chunk = chunkMap.remove(key);
            if(chunk != null) {
                if(!chunkStack.remove(chunk))
                    System.out.println("Error");
            }
            else
            {
                chunk = chunkStack.pop();
                chunkMap.remove(chunk.getChunkKey());
            }

            chunk.reuseChunk(col, row, zoom);
            if(chunkStack.size() != chunkMap.size())
                System.out.println("Errorrrr");
            return chunk;
        }

        public boolean isEmpty()
        {
            if(chunkStack.isEmpty() != chunkMap.isEmpty())
                throw new RuntimeException("Chunk keeper has issue with keeping chunks. Chunks do not match");
            return chunkStack.isEmpty();
        }

        public void clear()
        {
            chunkMap.clear();
            chunkStack.clear();
        }


    }

    private static final ChunkProvider provider = new ChunkProvider();

    private final FastNoise fn;
    private final Map<Long, NoiseChunkInterface> loadedNoiseMap;
    private final ReusableChunkKeeper keeper;

    private int chunkWidth;
    private int chunkHeight;
    private float zoom;

    PaintInterface pi;
    private ChunkProvider()
    {
        zoom = 1;
        loadedNoiseMap = new HashMap<>();
        keeper = new ReusableChunkKeeper();

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

    public void preload(int left, int top, int width, int height)
    {
        // TODO
    }

    public NoiseChunkInterface requestNoiseChunk(int col, int row, boolean paintUpdate)
    {
        long key = NoiseChunkInterface.getChunkKey(col, row);

        if(loadedNoiseMap.containsKey(key))
        {
            return loadedNoiseMap.get(key);
        }
        else{
            NoiseChunkInterface noiseChunk;
            if(keeper.isEmpty())
                noiseChunk = new NoiseChunk("Chunk" + col + "-" + row, fn, col, row, chunkWidth, chunkHeight, zoom);
            else
            {
                noiseChunk = keeper.reuseChunk(col, row, zoom);
            }

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

    public void clearMap(boolean reusable)
    {
        for(NoiseChunkInterface chunk: loadedNoiseMap.values()) {
            chunk.stopChunk();

        }
        keeper.clear();
        if(reusable)
            keeper.keepAllChunks(loadedNoiseMap);
        loadedNoiseMap.clear();
    }

    public void dimensionChanged(int chunkWidth, int chunkHeight)
    {
        clearMap(false);
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
    }

    public void zoomChanged(float zoom)
    {
        clearMap(true);
        this.zoom = zoom;
    }
}
