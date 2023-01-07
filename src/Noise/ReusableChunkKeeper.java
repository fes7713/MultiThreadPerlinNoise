package Noise;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ReusableChunkKeeper
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
            chunkStack.remove(chunk);
        }
        else
        {
            chunk = chunkStack.pop();
            chunkMap.remove(chunk.getChunkKey());
        }

        chunk.reuseChunk(col, row, zoom);
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
