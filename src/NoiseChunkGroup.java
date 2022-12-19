import java.awt.*;
import java.lang.Math;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

public class NoiseChunkGroup implements NoiseChunkInterface, NoiseChunkGroupInterface{
    private final String name;
    private NoiseChunkInterface[][] chunkTable;
    private final FastNoise fn;

    private int chunkX;
    private int chunkY;

    private int tableWidth;
    private int tableHeight;

    private int canvasWidth;
    private int canvasHeight;

    private int left;
    private int top;

    private float noiseMax;
    private float noiseMin;

    private final Semaphore semaphore;
    ThreadPoolExecutor executor;

    public NoiseChunkGroup(String name, FastNoise fn, int tableWidth, int tableHeight) {
        this.name = name;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;

        chunkTable = new NoiseChunk[tableWidth][tableHeight];
        this.fn = fn;

        left = 0;
        top = 0;
        canvasWidth = 1000;
        canvasHeight = 1000;

        semaphore = new Semaphore(tableWidth * tableHeight);

        initTable();

        widthChanged();
        heightChanged();
    }

    public NoiseChunkGroup(FastNoise fn, int tableWidth, int tableHeight) {
        this("Default", fn, tableWidth, tableHeight);
    }

    public NoiseChunkGroup(int tableWidth, int tableHeight) {
        this(new FastNoise(), tableWidth, tableHeight);
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);
    }

    private void initTable()
    {
        int width = (int)Math.ceil(canvasWidth / (double)tableWidth);
        int height = (int)Math.ceil(canvasHeight / (double)tableHeight);

        for (int i = 0; i < tableWidth; i++)
        {
            for (int j = 0; j < tableHeight; j++)
            {
                chunkTable[i][j] = new NoiseChunk(name + i + "-" + j, fn, i, j, width, height, semaphore);
            }
        }
    }

    public String getName()
    {
        return name;
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

    public void setWidth(int canvasWidth)
    {
        this.canvasWidth = canvasWidth;
        widthChanged();
    }

    public void setHeight(int canvasHeight)
    {
        this.canvasHeight = canvasHeight;
        heightChanged();
    }

    public void setDimension(int canvasWidth, int canvasHeight)
    {
        setWidth(canvasWidth);
        setHeight(canvasHeight);
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setLeft(left);
            }
        }
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
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
        Runnable afterTasks = () -> {
            try {
                semaphore.acquire(tableHeight * tableWidth);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            setNoiseRange(noiseMax, noiseMin);
            updateImage(pi);
            semaphore.release(tableHeight * tableWidth);
        };
        new Thread(afterTasks).start();
    }

    public void updateImage(PaintInterface pi)
    {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].updateImage(pi);
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

    @Override
    public void setChunkX(int chunkX) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setChunkX(chunkX + i);
            }
        }
    }

    @Override
    public void setChunkY(int chunkY) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setChunkY(chunkY + j);
            }
        }
    }

    private <E> void arrayCopy(E[][] src, int srcX, int srcY, int width, int height, E[][] dest, int destX, int destY)
    {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                dest[destX + i][destY + j] = src[srcX + i][srcY + j];
            }
        }
    }

    private void syncChunkCoordinate()
    {
        for(int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setChunkX(i + chunkX);
                chunkTable[i][j].setChunkY(j + chunkY);
            }
        }
    }

    @Override
    public NoiseChunkInterface[][] pushLeft(NoiseChunkGroup ncg, int length) {
        return new NoiseChunkInterface[0][];
    }

    /*/
     **     ******         **
     ** ->  ******    ->   **
     **     ******         **
     **     ******         **
     */
    @Override
    public NoiseChunkInterface[][] pushLeft(NoiseChunkInterface[][] pushedChunks) {
        return new NoiseChunkInterface[0][];
    }

    @Override
    public NoiseChunkInterface[][] pushTop(NoiseChunkGroup ncg, int length) {
        return new NoiseChunkInterface[0][];
    }

    @Override
    public NoiseChunkInterface[][] pushTop(NoiseChunkInterface[][] pushedChunks) {
        return new NoiseChunkInterface[0][];
    }

    /*/
     **     ******         **
     ** <-  ******    <-   **
     **     ******         **
     **     ******         **
     */

    public NoiseChunkInterface[][] pushRight(NoiseChunkGroup ncg, int length){
        NoiseChunkInterface[][] inputChunkTable = new NoiseChunkInterface[length][tableHeight];
        arrayCopy(ncg.chunkTable, 0, 0, length, tableHeight, inputChunkTable, 0, 0);
        return pushRight(inputChunkTable);
    }
    @Override
    public NoiseChunkInterface[][] pushRight(NoiseChunkInterface[][] pushedChunks) {

        if(pushedChunks.length == 0 || pushedChunks[0].length == 0)
            throw new IllegalArgumentException("Chunk dimension cannot be zero");
        if(tableHeight != pushedChunks[0].length)
            throw new IllegalArgumentException("Chunk dimension does not match");

        NoiseChunkInterface[][] retuningChunkTable;
        if(tableWidth > pushedChunks.length)
        {
            retuningChunkTable = new NoiseChunkInterface[pushedChunks.length][tableHeight];
            // Create retuning array
            arrayCopy(chunkTable, 0, 0, pushedChunks.length, tableHeight, retuningChunkTable, 0, 0);
            // Shift chunkTable by pushedChunks.length
            arrayCopy(
                    chunkTable, pushedChunks.length, 0, tableWidth - pushedChunks.length, tableHeight,
                    chunkTable, 0, 0
            );
            // Copy pushedChunk to the right side of chunkTable
            arrayCopy(
                    pushedChunks, 0, 0, pushedChunks.length, tableHeight,
                    chunkTable, tableWidth - pushedChunks.length, 0
            );

        }
        // Replace all
        else if(tableWidth == pushedChunks.length)
        {
            retuningChunkTable = chunkTable;
            chunkTable = pushedChunks;

        }
        // Pushed chunk table is bigger than existing table
        else{
            retuningChunkTable = new NoiseChunkInterface[pushedChunks.length][tableHeight];

            arrayCopy(chunkTable, 0, 0, tableWidth, tableHeight, retuningChunkTable, 0, 0);
            arrayCopy(
                    pushedChunks, pushedChunks.length - tableWidth, 0, tableWidth, tableHeight,
                    chunkTable, 0, 0
            );
            arrayCopy(
                    pushedChunks, 0 ,0, pushedChunks.length - tableWidth, tableHeight,
                    retuningChunkTable, tableWidth, 0
            );
        }
        syncChunkCoordinate();

        return retuningChunkTable;
    }

    @Override
    public NoiseChunkInterface[][] pushBottom(NoiseChunkGroup ncg, int length) {
        return new NoiseChunkInterface[0][];
    }

    @Override
    public NoiseChunkInterface[][] pushBottom(NoiseChunkInterface[][] pushedChunks) {
        return new NoiseChunkInterface[0][];
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableWidth; i++) {
            sb.append("[");
            for (int j = 0; j < tableHeight; j++) {
                sb.append(chunkTable[i][j]);
                sb.append(", ");
            }
            sb.append("], \n");
        }
        return sb.toString();
    }
}
