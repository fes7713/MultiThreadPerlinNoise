public class NoiseChunkManager {
    private int borderThickness;

    private NoiseChunkInterface[][] chunkLoadTable;

    private final int MAIN_CHUNK_TABLE_SIZE = 5;

    private int left;
    private int top;

    private int size;

    public NoiseChunkManager(int borderThickness)
    {
        chunkLoadTable = new NoiseChunkInterface[borderThickness * 2 + 1][borderThickness * 2 + 1];
        left = 0;
        top = 0;
        size = borderThickness * 2 + 1;
    }

    public void initTable()
    {
        FastNoise fn =  new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);

        chunkLoadTable[borderThickness][borderThickness] =
                    new NoiseChunkGroup(fn, MAIN_CHUNK_TABLE_SIZE, MAIN_CHUNK_TABLE_SIZE);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(i == borderThickness && j == borderThickness)
                    continue;
                if(i == borderThickness)
                    chunkLoadTable[i][j] = new NoiseChunkGroup(fn, MAIN_CHUNK_TABLE_SIZE, 1);
                else if(j == borderThickness)
                    chunkLoadTable[i][j] = new NoiseChunkGroup(fn, 1, MAIN_CHUNK_TABLE_SIZE);
                else
                    chunkLoadTable[i][j] = new NoiseChunkGroup(fn, 1, 1);


            }
        }


    }
}
