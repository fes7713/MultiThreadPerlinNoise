import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NoiseMapPanel extends JPanel implements ComponentListener, MouseMotionListener, MouseListener {
    private final NoiseChunkInterface nci;

    private int startX;
    private int startY;
    private int startLeft;
    private int startTop;

    public NoiseMapPanel(NoiseChunkInterface nci)
    {
        this.nci = nci;
        addComponentListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
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

//        nci.setDimension(width, height);

//        nci.updateChunk(
//                this::repaint,
//                nci::setNoiseRange
//        );
//
//        NoiseChunkGroup newNcg = new NoiseChunkGroup("New", new FastNoise(), 2, 5);
//        newNcg.updateChunk(null);
//        ((NoiseChunkGroup)nci).pushRight(newNcg, 2);
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
        System.out.println(e.getX());
        System.out.println(e.getY());
        int diffX = e.getX() - startX;
        int diffY = e.getY() - startY;

        nci.setLeft(startLeft + diffX);
        nci.setTop(startTop + diffY);

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
        startLeft = nci.getLeft();
        startTop = nci.getTop();
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
