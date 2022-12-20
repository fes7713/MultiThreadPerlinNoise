import Noise.ChunkProvider;
import Noise.NoiseChunkGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NoiseMapPanel extends JPanel implements ComponentListener, MouseMotionListener, MouseListener {
    private final ChunkProvider provider = ChunkProvider.getInstance();
    private NoiseChunkGroup ncg;

    private int startX;
    private int startY;
    private int startLeft;
    private int startTop;

    private int tableWidth;
    private int tableHeight;

    public NoiseMapPanel(NoiseChunkGroup ncg)
    {
        this.ncg = ncg;


        tableWidth = tableHeight = 5;
        addComponentListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public NoiseMapPanel()
    {
        this(new NoiseChunkGroup("Chunk",  100, 200,5, 5));
        ncg.updateChunk(
            this::repaint
        );
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        ncg.drawImage(g2d);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int width = e.getComponent().getWidth();
        int height = e.getComponent().getHeight();

        provider.dimensionChanged((int)Math.ceil(width / (double)tableWidth), (int)Math.ceil(height / (double)tableHeight));
        ncg = new NoiseChunkGroup("Chunk",  width, height,tableWidth, tableHeight);
        System.out.println(ncg.getChunkWidth());
        System.out.println(ncg.getChunkHeight());

        ncg.updateChunk(this::repaint);
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
//        ncg.setPixelShiftX(startLeft);
//        ncg.setPixelShiftY(startTop);

        ncg.setChunkShiftX(startLeft / ncg.getChunkWidth());
        ncg.setChunkShiftY(startTop / ncg.getChunkHeight());
        ncg.setPixelShiftX(startLeft % ncg.getChunkWidth());
        ncg.setPixelShiftY(startTop % ncg.getChunkHeight());


//        System.out.println("Chunk X " + startLeft/ncg.getChunkWidth());
//        System.out.println("Chunk Y " + startTop/ncg.getChunkHeight());
//        System.out.println("Pixel X " + startLeft%ncg.getChunkWidth());
//        System.out.println("Pixel Y " + startTop%ncg.getChunkHeight());
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
