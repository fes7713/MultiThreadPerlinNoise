import Noise.ChunkProvider;
import Noise.ColorProvider;
import Noise.NoiseChunkGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.stream.Stream;

public class NoiseMapPanel extends JPanel implements ComponentListener, MouseMotionListener, MouseListener, MouseWheelListener {
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

    private int wheelCount;

    private int mouseX;
    private int mouseY;

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
        addMouseWheelListener(this);
        colorProvider.setPaintInterface(this::updateImage);

        colorProvider.showColorEditor();
        wheelCount = 0;
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

        g2d.drawLine(mouseX, 0, mouseX, this.getHeight());
        g2d.drawLine(0, mouseY, this.getWidth(), mouseY);

        float zoom = (float)Math.pow(1.1, wheelCount);
        g2d.drawString("(" + (int)((mouseX - startLeft) * zoom)   + ", " + (int)((mouseY - startTop) * zoom) + ")", mouseX + 20, mouseY + 20);
    }

    private void updateChunkGroups()
    {
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

        mainGroup.loadChunks(- startLeft / mainGroup.getChunkWidth(), - startTop / mainGroup.getChunkHeight(), true);


        Stream.of(mainGroup, horizontalEdgeGroup, verticalEdgeGroup, cornerGroup)
                .forEach(group -> {
                    group.setChunkShiftX(startLeft / mainGroup.getChunkWidth());
                    group.setChunkShiftY(startTop / mainGroup.getChunkHeight());
                    group.setPixelShiftX(startLeft % mainGroup.getChunkWidth());
                    group.setPixelShiftY(startTop % mainGroup.getChunkHeight());
                });

        repaint();
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

        updateChunkGroups();

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
        mouseX = e.getX();
        mouseY = e.getY();
        int diffX = mouseX - startX;
        int diffY = mouseY - startY;
        startX = mouseX;
        startY = mouseY;
        startLeft += diffX;
        startTop += diffY;

        updateChunkGroups();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        System.out.println();
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        int preWheelCount = wheelCount;
        wheelCount -= e.getWheelRotation();
        System.out.println(wheelCount);

        float zoom = (float)Math.pow(1.1, wheelCount);
        float preZoom = (float)Math.pow(1.1, preWheelCount);

        double x = (int)((mouseX - startLeft) * zoom);
        double y = (int)((mouseY - startTop) * zoom);

        double preX = (int)((mouseX - startLeft) * preZoom);
        double preY = (int)((mouseY - startTop) * preZoom);

        System.out.println("old " + startX);
        startLeft = (int)(mouseX - (mouseX - startLeft) * Math.pow(1.1, e.getWheelRotation()));
        startTop = (int)(mouseY - (mouseY - startTop) * Math.pow(1.1, e.getWheelRotation()));

        System.out.println("new " + startX);

        chunkProvider.zoomChanged(zoom);


        updateChunkGroups();
    }
}
