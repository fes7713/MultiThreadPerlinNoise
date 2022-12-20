import java.awt.*;
import java.lang.Math;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NoiseChunkGroup implements NoiseChunkInterface, NoiseChunkGroupInterface{
    private final String name;
    private NoiseChunkInterface[][] chunkTable;

    private final int chunkX;
    private final int chunkY;
    private final int chunkWidth;
    private final int chunkHeight;

//    private int chunkShiftX;
//    private int chunkShiftY;
    private int pixelShiftX;
    private int pixelShiftY;

    private final int tableWidth;
    private final int tableHeight;

    private final int canvasWidth;
    private final int canvasHeight;

    private final Semaphore semaphore;
    private final Lock lock;

    private final ChunkProvider provider;

    public NoiseChunkGroup(String name, int canvasWidth, int canvasHeight, int tableWidth, int tableHeight, Semaphore semaphore) {
        this.name = name;
        this.tableWidth = tableWidth;
        this.tableHeight = tableHeight;

        chunkTable = new NoiseChunk[tableWidth][tableHeight];

        chunkX = chunkY = 0;
//        chunkShiftX = chunkShiftY = 0;
//        pixelShiftX = pixelShiftY = 0;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        chunkWidth = getChunkWidth();
        chunkHeight = getChunkHeight();



        this.semaphore = semaphore;
        lock = new ReentrantLock();
        provider = ChunkProvider.getInstance();
        initTable();
    }

    public NoiseChunkGroup(String name,  int canvasWidth, int canvasHeight, int tableWidth, int tableHeight) {
        this(name, canvasWidth, canvasHeight, tableWidth, tableHeight, new Semaphore(tableWidth * tableHeight));
    }

    public NoiseChunkGroup(int canvasWidth, int canvasHeight, int tableWidth, int tableHeight) {
        this("Default", canvasWidth, canvasHeight, tableWidth, tableHeight);
    }

    public NoiseChunkGroup(int canvasWidth, int canvasHeight, int tableWidth, int tableHeight, Semaphore semaphore) {
        this("Default" ,canvasWidth, canvasHeight, tableWidth, tableHeight, semaphore);
    }

    private void initTable()
    {
        for (int i = 0; i < tableWidth; i++)
        {
            for (int j = 0; j < tableHeight; j++)
            {
                chunkTable[i][j] = provider.requestNoiseChunk(i, j, semaphore);
//                        new NoiseChunk(name + i + "-" + j, fn, i, j, chunkWidth, chunkHeight, semaphore);
            }
        }
    }

    public String getName()
    {
        return name;
    }

    @Override
    public int getChunkX() {
        return chunkX;
    }

    @Override
    public int getChunkY() {
        return chunkY;
    }

    @Override
    public void setChunkShiftX(int chunkShiftX) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setChunkShiftX(chunkShiftX);
            }
        }
    }

    @Override
    public void setChunkShiftY(int chunkShiftY) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setChunkShiftY(chunkShiftY);
            }
        }
    }

    @Override
    public int getPixelShiftX() {
        return pixelShiftX;
    }

    @Override
    public void setPixelShiftX(int pixelShiftX) {
        this.pixelShiftX = pixelShiftX;
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setPixelShiftX(pixelShiftX);
            }
        }
    }

    @Override
    public int getPixelShiftY() {
        return pixelShiftY;
    }

    @Override
    public void setPixelShiftY(int pixelShiftY) {
        this.pixelShiftY = pixelShiftY;
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].setPixelShiftY(pixelShiftY);
            }
        }
    }

    @Override
    public void updateChunk(PaintInterface pi) {
        for (int i = 0; i < tableWidth; i++) {
            for (int j = 0; j < tableHeight; j++) {
                chunkTable[i][j].updateChunk(pi);
            }
        }

        Runnable afterTasks = () -> {
            try {
                semaphore.acquire(tableHeight * tableWidth);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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

    private <E> void arrayCopy(E[][] src, int srcX, int srcY, int width, int height, E[][] dest, int destX, int destY)
    {
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                dest[destX + i][destY + j] = src[srcX + i][srcY + j];
            }
        }
    }

    private <E> void arrayReverseCopy(E[][] src, int srcX, int srcY, int width, int height, E[][] dest, int destX, int destY)
    {
        for(int i = width - 1; i >= 0; i--) {
            for (int j = height - 1; j >= 0; j--) {
                dest[destX + i][destY + j] = src[srcX + i][srcY + j];
            }
        }
    }


    /*/
     **     ******         **
     ** ->  ******    ->   **
     **     ******         **
     **     ******         **
     */
    @Override
    public NoiseChunkInterface[][] pushLeft(NoiseChunkGroup ncg, int length) {
        NoiseChunkInterface[][] inputChunkTable = new NoiseChunkInterface[length][tableHeight];
        arrayCopy(ncg.chunkTable, ncg.tableWidth - length, 0, length, tableHeight, inputChunkTable, 0, 0);
        return pushLeft(inputChunkTable);
    }

    @Override
    public NoiseChunkInterface[][] pushLeft(NoiseChunkInterface[][] pushedChunks) {
        if(pushedChunks.length == 0 || pushedChunks[0].length == 0)
            throw new IllegalArgumentException("Chunk dimension cannot be zero");
        if(tableHeight != pushedChunks[0].length)
            throw new IllegalArgumentException("Chunk dimension does not match");

        NoiseChunkInterface[][] retuningChunkTable;
        if(tableWidth > pushedChunks.length)
        {
            retuningChunkTable = new NoiseChunkInterface[pushedChunks.length][tableHeight];
            // Create retuning array
            arrayCopy(
                    chunkTable, tableWidth - pushedChunks.length, 0, pushedChunks.length, tableHeight,
                    retuningChunkTable, 0, 0
            );
            // Shift chunkTable by pushedChunks.length
            arrayReverseCopy(
                    chunkTable, 0, 0, tableWidth - pushedChunks.length, tableHeight,
                    chunkTable, pushedChunks.length, 0
            );
            // Copy pushedChunk to the right side of chunkTable
            arrayCopy(
                    pushedChunks, 0, 0, pushedChunks.length, tableHeight,
                    chunkTable, 0, 0
            );

        }
        // Replace all
        else if(tableWidth == pushedChunks.length)
        {
            retuningChunkTable = chunkTable;
            chunkTable = pushedChunks;

        }
        // Pushed chunk table is bigger than existing table
        else {
            retuningChunkTable = new NoiseChunkInterface[pushedChunks.length][tableHeight];

            // Copy chunk table to returning chunk table
            arrayCopy(
                    chunkTable, 0, 0, tableWidth, tableHeight,
                    retuningChunkTable, pushedChunks.length - tableWidth, 0
            );
            // Copy the pushed chunk table to the chunk table
            arrayCopy(
                    pushedChunks, 0, 0, tableWidth, tableHeight,
                    chunkTable, 0, 0
            );
            arrayCopy(
                    pushedChunks, tableWidth, 0, pushedChunks.length - tableWidth, tableHeight,
                    retuningChunkTable, 0, 0
            );
        }

        return retuningChunkTable;
    }

    /*/
    ******
    ******
      |
      V
    ******
    ******
    ******
    ******
      |
      V
    ******
    ******
     */
    @Override
    public NoiseChunkInterface[][] pushTop(NoiseChunkGroup ncg, int length) {
        NoiseChunkInterface[][] inputChunkTable = new NoiseChunkInterface[tableWidth][length];
        arrayCopy(ncg.chunkTable, 0, ncg.tableHeight - length, tableWidth, length, inputChunkTable, 0, 0);
        return pushTop(inputChunkTable);
    }

    @Override
    public NoiseChunkInterface[][] pushTop(NoiseChunkInterface[][] pushedChunks) {

        if(pushedChunks.length == 0 || pushedChunks[0].length == 0)
            throw new IllegalArgumentException("Chunk dimension cannot be zero");
        if(tableWidth != pushedChunks.length)
            throw new IllegalArgumentException("Chunk dimension does not match");

        NoiseChunkInterface[][] retuningChunkTable;
        if(tableHeight > pushedChunks[0].length)
        {
            retuningChunkTable = new NoiseChunkInterface[tableWidth][pushedChunks[0].length];
            // Create retuning array
            arrayCopy(
                    chunkTable, 0, tableHeight - pushedChunks[0].length, tableWidth, pushedChunks[0].length,
                    retuningChunkTable, 0, 0
            );
            // Shift chunkTable by pushedChunks.length
            arrayReverseCopy(
                    chunkTable, 0, 0, tableWidth, tableHeight - pushedChunks[0].length,
                    chunkTable, 0, pushedChunks[0].length
            );
            // Copy pushedChunk to the right side of chunkTable
            arrayCopy(
                    pushedChunks, 0, 0, tableWidth, pushedChunks[0].length,
                    chunkTable, 0, 0
            );

        }
        // Replace all
        else if(tableHeight == pushedChunks[0].length)
        {
            retuningChunkTable = chunkTable;
            chunkTable = pushedChunks;

        }
        // Pushed chunk table is bigger than existing table
        else{
            retuningChunkTable = new NoiseChunkInterface[tableWidth][pushedChunks[0].length];

            // Copy chunk table to returning chunk table
            arrayCopy(
                    chunkTable, 0, 0, tableWidth, tableHeight,
                    retuningChunkTable, 0, pushedChunks[0].length - tableHeight
            );
            // Copy the pushed chunk table to the chunk table
            arrayCopy(
                    pushedChunks, 0, 0, tableWidth, tableHeight,
                    chunkTable, 0, 0
            );
            arrayCopy(
                    pushedChunks, 0 ,tableHeight, tableWidth, pushedChunks[0].length - tableHeight,
                    retuningChunkTable, 0, 0
            );
        }

        return retuningChunkTable;
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

        return retuningChunkTable;
    }

    /*/
    ******
    ******
      U
      |
    ******
    ******
    ******
    ******
      U
      |
    ******
    ******
     */

    @Override
    public NoiseChunkInterface[][] pushBottom(NoiseChunkGroup ncg, int length) {
        NoiseChunkInterface[][] inputChunkTable = new NoiseChunkInterface[tableWidth][length];
        arrayCopy(ncg.chunkTable, 0, 0, tableWidth, length, inputChunkTable, 0, 0);
        return pushBottom(inputChunkTable);
    }

    @Override
    public NoiseChunkInterface[][] pushBottom(NoiseChunkInterface[][] pushedChunks) {
        if(pushedChunks.length == 0 || pushedChunks[0].length == 0)
            throw new IllegalArgumentException("Chunk dimension cannot be zero");
        if(tableWidth != pushedChunks.length)
            throw new IllegalArgumentException("Chunk dimension does not match");

        NoiseChunkInterface[][] retuningChunkTable;
        if(tableHeight > pushedChunks[0].length)
        {
            retuningChunkTable = new NoiseChunkInterface[tableWidth][pushedChunks[0].length];
            // Create retuning array
            arrayCopy(chunkTable, 0, 0, tableWidth, pushedChunks[0].length, retuningChunkTable, 0, 0);
            // Shift chunkTable by pushedChunks.length
            arrayCopy(
                    chunkTable, 0, pushedChunks[0].length, tableWidth, tableHeight - pushedChunks[0].length,
                    chunkTable, 0, 0
            );
            // Copy pushedChunk to the right side of chunkTable
            arrayCopy(
                    pushedChunks, 0, 0, tableWidth, pushedChunks[0].length,
                    chunkTable, 0, tableHeight - pushedChunks[0].length
            );

        }
        // Replace all
        else if(tableHeight == pushedChunks[0].length)
        {
            retuningChunkTable = chunkTable;
            chunkTable = pushedChunks;

        }
        // Pushed chunk table is bigger than existing table
        else{
            retuningChunkTable = new NoiseChunkInterface[tableWidth][pushedChunks[0].length];

            arrayCopy(chunkTable, 0, 0, tableWidth, tableHeight, retuningChunkTable, 0, 0);
            arrayCopy(
                    pushedChunks, 0, pushedChunks[0].length - tableHeight, tableWidth, tableHeight,
                    chunkTable, 0, 0
            );
            arrayCopy(
                    pushedChunks, 0 ,0, tableWidth, pushedChunks[0].length - tableHeight,
                    retuningChunkTable, 0, tableHeight
            );
        }

        return retuningChunkTable;
    }

    @Override
    public int getChunkWidth() {
        return (int)Math.ceil(canvasWidth / (double)tableWidth);

    }

    @Override
    public int getChunkHeight() {
        return (int)Math.ceil(canvasHeight / (double)tableHeight);
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
