package map.Noise;

import java.util.*;

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
        // Error checking
        while(chunkStack.size() != chunkMap.size())
            reconstructData();
    }

    public NoiseChunkInterface reuseChunk(int col, int row, float zoom)
    {
        long key = NoiseChunkInterface.getChunkKey(col, row);

        synchronized(this)
        {
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

            // Error checking
            while(chunkStack.size() != chunkMap.size())
                reconstructData();

            chunk.reuseChunk(col, row, zoom);
            return chunk;
        }
    }

    public boolean isEmpty()
    {
        if(chunkStack.isEmpty() != chunkMap.isEmpty())
        {
            while(chunkStack.size() != chunkMap.size())
            {
                reconstructData();
            }
        }

        return chunkStack.isEmpty();
    }

    private void reconstructData(){
        System.err.println("Error in chunk keeper. Reconstructing data structure in chunk keeper");
        List<NoiseChunkInterface> chunks = new ArrayList<>();
        NoiseChunkInterface chunk;

        synchronized (this)
        {
            while(!chunkStack.isEmpty()){
                chunk = chunkStack.pop();
                if(chunkMap.containsValue(chunk))
                    chunks.add(chunk);
            }
            chunkStack.clear();
            chunkMap.clear();
            chunkStack.addAll(chunks);
            chunks.forEach(c -> {
                chunkMap.put(c.getChunkKey(), c);
            });
        }

    }

    public void clear()
    {
        synchronized (this)
        {
            chunkMap.clear();
            chunkStack.clear();
        }

    }
}
