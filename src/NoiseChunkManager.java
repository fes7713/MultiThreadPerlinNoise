import java.lang.Math;

public class NoiseChunkManager {
    private NoiseChunk[][] chunkTable;
    private final FastNoise fn;

    private int tableWidth;
    private int tableHeight;

    private int canvasWidth;
    private int canvasHeight;

    private float left;
    private float top;

    public NoiseChunkManager(int tableWidth, int tableHeight) {
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;

        chunkTable = new NoiseChunk[tableWidth][tableHeight];
        fn = new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);

        left = 0;
        top = 0;
        initTable();
    }

    private void initTable()
    {
        int width = (int)Math.ceil(canvasWidth / (double)tableWidth);
        int height = (int)Math.ceil(canvasHeight / (double)tableHeight);

        for (int i = 0; i < tableWidth; i++)
        {
            for (int j = 0; j < tableHeight; j++)
            {
                chunkTable[i][j] = new NoiseChunk(fn, i, j, left, top, width, height);
             }
        }
    }

    private void widthChanged()
    {
        int width = (int)Math.ceil(canvasWidth / (double)tableWidth);
        for (int i = 0; i < tableWidth; i++)
        {
            for (int j = 0; j < tableHeight; j++)
            {
                chunkTable[i][j].setWidth(width);
            }
        }
    }

    private void heightChanged()
    {
        int height = (int)Math.ceil(canvasHeight / (double)tableHeight);
        for (int i = 0; i < tableWidth; i++)
        {
            for (int j = 0; j < tableHeight; j++)
            {
                chunkTable[i][j].setHeight(height);
            }
        }
    }

    public int getTableWidth() {
        return tableWidth;
    }

    public void setTableWidth(int tableWidth) {
        this.tableWidth = tableWidth;
        chunkTable = new NoiseChunk[tableWidth][tableHeight];
        widthChanged();
    }

    public int getTableHeight() {
        return tableHeight;
    }

    public void setTableHeight(int tableHeight) {
        this.tableHeight = tableHeight;
        chunkTable = new NoiseChunk[tableWidth][tableHeight];
        heightChanged();
    }

    public void setCanvasDimension(int canvasWidth, int canvasHeight)
    {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        widthChanged();
        heightChanged();
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setLeft(left);
            }
        }
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setLeft(left);
            }
        }
    }
}
