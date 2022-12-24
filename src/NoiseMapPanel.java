import Noise.ChunkProvider;
import Noise.ColorProvider;
import Noise.NoiseChunkGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.stream.Stream;

public class NoiseMapPanel extends JPanel implements ComponentListener, MouseMotionListener, MouseListener {
    private final ChunkProvider chunkProvider = ChunkProvider.getInstance(this::repaint);
    private final ColorProvider colorProvider = ColorProvider.getInstance();
    private NoiseChunkGroup mainGroup;
    private NoiseChunkGroup verticalEdgeGroup;
    private NoiseChunkGroup horizontalEdgeGroup;
    private NoiseChunkGroup cornerGroup;

    private int startX;
    private int startY;
    private int startLeft;
    private int startTop;

    private final int tableWidth;
    private final int tableHeight;

    private static final int CHUNK_SIZE = 5;

    public NoiseMapPanel(NoiseChunkGroup ncg)
    {
        this.mainGroup = ncg;

        tableWidth = tableHeight = CHUNK_SIZE;

        verticalEdgeGroup = new NoiseChunkGroup("Vertical Chunk",  100, 200,CHUNK_SIZE, 1);
        horizontalEdgeGroup = new NoiseChunkGroup("Horizontal Chunk",  100, 200,1, CHUNK_SIZE);
        cornerGroup = new NoiseChunkGroup("Horizontal Chunk",  100, 100,1, 1);

        verticalEdgeGroup.loadChunks(0, 0, true);
        horizontalEdgeGroup.loadChunks(0, 0, true);
        cornerGroup.loadChunks(0, 0, true);
        addComponentListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        colorProvider.setPaintInterface(this::updateImage);

        colorProvider.showColorEditor();
    }

    public NoiseMapPanel()
    {
        this(new NoiseChunkGroup("Chunk",  100, 200,CHUNK_SIZE, CHUNK_SIZE));
        mainGroup.loadChunks(0, 0, true);
    }

    public void updateImage()
    {
        mainGroup.updateImage(this::repaint);
        verticalEdgeGroup.updateImage(this::repaint);
        horizontalEdgeGroup.updateImage(this::repaint);
        cornerGroup.updateImage(this::repaint);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        verticalEdgeGroup.drawImage(g2d);
        horizontalEdgeGroup.drawImage(g2d);
        cornerGroup.drawImage(g2d);
        mainGroup.drawImage(g2d);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int width = e.getComponent().getWidth();
        int height = e.getComponent().getHeight();

        chunkProvider.dimensionChanged((int)Math.ceil(width / (double)tableWidth), (int)Math.ceil(height / (double)tableHeight));
        mainGroup =
                new NoiseChunkGroup("Main",  width, height,tableWidth, tableHeight);
        verticalEdgeGroup =
                new NoiseChunkGroup("Vertical",  width, mainGroup.getChunkHeight(),tableWidth, 1);
        horizontalEdgeGroup =
                new NoiseChunkGroup("Horizontal",  mainGroup.getChunkWidth(), height,1, tableHeight);
        cornerGroup =
                new NoiseChunkGroup("Corner",  mainGroup.getChunkWidth(), mainGroup.getChunkHeight(),1, 1);

//        mainGroup.updateChunk(this::repaint);
        mainGroup.loadChunks(0, 0, true);
        verticalEdgeGroup.loadChunks(0, 0, true);
        horizontalEdgeGroup.loadChunks(0, 0, true);
        cornerGroup.loadChunks(0, 0, true);
        System.out.println("Resize");
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

    @Override
    public void mouseDragged(MouseEvent e) {
        int diffX = e.getX() - startX;
        int diffY = e.getY() - startY;
        startX = e.getX();
        startY = e.getY();
        startLeft += diffX;
        startTop += diffY;

        Stream.of(mainGroup, horizontalEdgeGroup, verticalEdgeGroup, cornerGroup)
                .forEach(group -> {
                    group.setChunkShiftX(startLeft / mainGroup.getChunkWidth());
                    group.setChunkShiftY(startTop / mainGroup.getChunkHeight());
                    group.setPixelShiftX(startLeft % mainGroup.getChunkWidth());
                    group.setPixelShiftY(startTop % mainGroup.getChunkHeight());
                });

        mainGroup.loadChunks(- startLeft / mainGroup.getChunkWidth(), - startTop / mainGroup.getChunkHeight(), true);

        if(startLeft < 0)
        {
            System.out.println("Right");
            horizontalEdgeGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() + CHUNK_SIZE , - startTop / mainGroup.getChunkHeight(), true);
            if(startTop < 0)
                cornerGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() + CHUNK_SIZE , - startTop / mainGroup.getChunkHeight() + CHUNK_SIZE, true);
            else
                cornerGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() + CHUNK_SIZE , - startTop / mainGroup.getChunkHeight() - 1, true);
        }
        else{
            System.out.println("Left");
            horizontalEdgeGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() - 1, - startTop / mainGroup.getChunkHeight(), true);
            if(startTop < 0)
                cornerGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() - 1 , - startTop / mainGroup.getChunkHeight() + CHUNK_SIZE, true);
            else
                cornerGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() - 1 , - startTop / mainGroup.getChunkHeight() - 1, true);
        }

        if(startTop < 0)
        {
            System.out.println("Bottom");
            verticalEdgeGroup.loadChunks(- startLeft / mainGroup.getChunkWidth(), - startTop / mainGroup.getChunkHeight() + CHUNK_SIZE, true);

        }
        else{
            System.out.println("Top");
            verticalEdgeGroup.loadChunks(- startLeft / mainGroup.getChunkWidth(), - startTop / mainGroup.getChunkHeight() -1, true);

        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
