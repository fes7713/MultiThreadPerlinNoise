import Noise.ChunkProvider;
import Noise.NoiseChunkGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NoiseMapPanel extends JPanel implements ComponentListener, MouseMotionListener, MouseListener {
    private final ChunkProvider provider = ChunkProvider.getInstance();
    private NoiseChunkGroup nci;

    private int startX;
    private int startY;
    private int startLeft;
    private int startTop;

    private int tableWidth;
    private int tableHeight;

    public NoiseMapPanel(NoiseChunkGroup nci)
    {
        this.nci = nci;


        tableWidth = tableHeight = 5;
        addComponentListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public NoiseMapPanel()
    {
        this(new NoiseChunkGroup("Chunk",  100, 200,5, 5));
        nci.updateChunk(
            this::repaint
        );
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        nci.drawImage(g2d);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int width = e.getComponent().getWidth();
        int height = e.getComponent().getHeight();

        provider.dimensionChanged((int)Math.ceil(width / (double)tableWidth), (int)Math.ceil(height / (double)tableHeight));
        nci = new NoiseChunkGroup("Chunk",  width, height,tableWidth, tableHeight);
        System.out.println(nci.getChunkWidth());
        System.out.println(nci.getChunkHeight());

        nci.updateChunk(this::repaint);
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

        nci.setPixelShiftX(startLeft + diffX);
        nci.setPixelShiftY(startTop + diffY);

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
        startLeft = nci.getPixelShiftX();
        startTop = nci.getPixelShiftY();
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
