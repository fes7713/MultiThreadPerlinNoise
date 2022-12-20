import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class NoiseChunkManager implements ComponentListener {
    private int borderThickness;
    private final int MAIN_CHUNK_TABLE_SIZE = 5;

    private int left;
    private int top;

    private int size;

    NoiseChunkGroup mainGroup;
    Semaphore semaphore;


    public NoiseChunkManager(int borderThickness)
    {
        left = 0;
        top = 0;
        size = borderThickness * 2 + 1;
        semaphore = new Semaphore(MAIN_CHUNK_TABLE_SIZE * MAIN_CHUNK_TABLE_SIZE);
//        mainGroup = new NoiseChunkGroup(fn, 500, 500, MAIN_CHUNK_TABLE_SIZE, MAIN_CHUNK_TABLE_SIZE, semaphore);

    }



    @Override
    public void componentResized(ComponentEvent e) {
        int width = e.getComponent().getWidth();
        int height = e.getComponent().getHeight();


    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
