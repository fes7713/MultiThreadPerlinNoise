import java.awt.*;
import java.lang.Math;

public class NoiseChunkManager implements NoiseChunkInterface{
    private NoiseChunk[][] chunkTable;
    private final FastNoise fn;

    private int tableWidth;
    private int tableHeight;

    private int canvasWidth;
    private int canvasHeight;

    private float left;
    private float top;

    private float noiseMax;
    private float noiseMin;

    public NoiseChunkManager(int tableWidth, int tableHeight) {
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;

        chunkTable = new NoiseChunk[tableWidth][tableHeight];
        fn = new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);

        left = 0;
        top = 0;
        canvasWidth = 1000;
        canvasHeight = 1000;

        initTable();

        widthChanged();
        heightChanged();
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

    public void setDimension(int canvasWidth, int canvasHeight)
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
                chunkTable[i][j].setTop(top);
            }
        }
    }

    @Override
    public void updateChunk(PaintInterface pi, NoiseRangeInterface nri) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].updateChunk(pi, nri);
            }
        }
    }

    @Override
    public void drawImage(Graphics2D g2d) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].drawImage(g2d);
            }
        }
    }

    @Override
    public float getNoiseMax() {
        return noiseMax;
    }

    @Override
    public float getNoiseMin() {
        return noiseMin;
    }

    @Override
    public synchronized void setNoiseRange(float max, float min) {
        if(noiseMax < max)
            noiseMax = max;
        if(noiseMin > min)
            noiseMin = min;

        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableWidth; j++) {
                chunkTable[i][j].setNoiseRange(noiseMax, noiseMin);
            }
        }
    }
}
