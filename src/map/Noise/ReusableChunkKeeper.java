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
        if(chunkStack.isEmpty() != chunkMap.isEmpty())
            throw new RuntimeException("!!!!Chunk keeper has issue with keeping chunks. Chunks do not match");
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
        if(chunkStack.isEmpty() != chunkMap.isEmpty())
            throw new RuntimeException("AAAAAChunk keeper has issue with keeping chunks. Chunks do not match");
        chunk.reuseChunk(col, row, zoom);
        return chunk;
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
        System.out.println("reconstructData");
        List<NoiseChunkInterface> chunks = new ArrayList<>();
        NoiseChunkInterface chunk;

        while(!chunkStack.isEmpty()){
            chunk = chunkStack.pop();
            if(chunkMap.containsValue(chunk))
                chunks.add(chunk);
        }
        chunkStack.clear();
        chunkMap.clear();
        chunkStack.addAll(chunks);
        chunks.stream().forEach(c -> {
            chunkMap.put(c.getChunkKey(), c);
        });
    }

    public void clear()
    {
        chunkMap.clear();
        chunkStack.clear();
    }
}
