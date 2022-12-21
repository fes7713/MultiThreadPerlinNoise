import Noise.ChunkProvider;
import Noise.NoiseChunkGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NoiseMapPanel extends JPanel implements ComponentListener, MouseMotionListener, MouseListener {
    private final ChunkProvider provider = ChunkProvider.getInstance(this::repaint);
    private NoiseChunkGroup mainGroup;
    private NoiseChunkGroup verticalEdgeGroup;
    private NoiseChunkGroup horizontalEdgeGroup;

    private int startX;
    private int startY;
    private int startLeft;
    private int startTop;

    private int tableWidth;
    private int tableHeight;

    public NoiseMapPanel(NoiseChunkGroup ncg)
    {
        this.mainGroup = ncg;

        tableWidth = tableHeight = 5;
        verticalEdgeGroup = new NoiseChunkGroup("Vertical Chunk",  100, 200,5, 1);
        horizontalEdgeGroup = new NoiseChunkGroup("Horizontal Chunk",  100, 200,1, 5);

        addComponentListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public NoiseMapPanel()
    {
        this(new NoiseChunkGroup("Chunk",  100, 200,5, 5));
        mainGroup.loadChunks(0, 0, true);
//        mainGroup.updateChunk(
//            this::repaint
//        );
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        mainGroup.drawImage(g2d);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int width = e.getComponent().getWidth();
        int height = e.getComponent().getHeight();

        provider.dimensionChanged((int)Math.ceil(width / (double)tableWidth), (int)Math.ceil(height / (double)tableHeight));
        mainGroup =
                new NoiseChunkGroup("Main",  width, height,tableWidth, tableHeight);
        verticalEdgeGroup =
                new NoiseChunkGroup("Vertical",  width, mainGroup.getChunkHeight(),tableWidth, 1);
        horizontalEdgeGroup =
                new NoiseChunkGroup("Horizontal",  mainGroup.getChunkWidth(), height,1, tableHeight);

//        mainGroup.updateChunk(this::repaint);
        mainGroup.loadChunks(0, 0, true);
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
//        mainGroup.setPixelShiftX(startLeft);
//        mainGroup.setPixelShiftY(startTop);

        mainGroup.setChunkShiftX(startLeft / mainGroup.getChunkWidth());
        mainGroup.setChunkShiftY(startTop / mainGroup.getChunkHeight());
        mainGroup.setPixelShiftX(startLeft % mainGroup.getChunkWidth());
        mainGroup.setPixelShiftY(startTop % mainGroup.getChunkHeight());
        mainGroup.loadChunks(- startLeft / mainGroup.getChunkWidth(), - startTop / mainGroup.getChunkHeight(), true);


//        System.out.println("Chunk X " + startLeft/mainGroup.getChunkWidth());
//        System.out.println("Chunk Y " + startTop/mainGroup.getChunkHeight());
//        System.out.println("Pixel X " + startLeft%mainGroup.getChunkWidth());
//        System.out.println("Pixel Y " + startTop%mainGroup.getChunkHeight());
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
