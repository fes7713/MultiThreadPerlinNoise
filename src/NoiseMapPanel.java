import Noise.Array.PerlinNoiseArrayInterface;
import Noise.Array.VariableChanger;
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

    private int startLeftMinLimit = DEFAULT_LEFT_MIN_LIMIT;
    private int startLeftMaxLimit = DEFAULT_LEFT_MAX_LIMIT;
    private int startTopMinLimit = DEFAULT_TOP_MIN_LIMIT;
    private int startTopMaxLimit = DEFAULT_TOP_MAX_LIMIT;

    private final int tableWidth;
    private final int tableHeight;

    private int zoomCount;

    private int maxZoomCount = DEFAULT_ZOOM_MAX_LIMIT;
    private int minZoomCount = DEFAULT_ZOOM_MIN_LIMIT;

    private int mouseX;
    private int mouseY;

    private static final int CHUNK_SIZE = 5;
    private static final float ZOOM_RATIO = 2;

    private static final int DEFAULT_ZOOM_MAX_LIMIT = 4;
    private static final int DEFAULT_ZOOM_MIN_LIMIT = - 10;
    private static final int DEFAULT_LEFT_MIN_LIMIT = - 3000;
    private static final int DEFAULT_LEFT_MAX_LIMIT = 5000;
    private static final int DEFAULT_TOP_MIN_LIMIT = -2000;
    private static final int DEFAULT_TOP_MAX_LIMIT = 2000;

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
        PerlinNoiseArrayInterface.loadDefaultVariables(null);
        zoomCount = 0;
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

    public void showVariableChanger(){
        VariableChanger vc = new VariableChanger(this::updateImage);
        vc.showVariableChanger();
    }

    public void showColorEditor(){
        colorProvider.showColorEditor();
    }

    private float getZoom()
    {
        return (float)Math.pow(ZOOM_RATIO, zoomCount);
    }

    private boolean setZoomCount(int count)
    {
        if(minZoomCount <= count && count <= maxZoomCount)
        {
            zoomCount = count;
            return true;
        }
        else if(minZoomCount > count)
            zoomCount = minZoomCount;
        else
            zoomCount = maxZoomCount;
        return false;
    }

    private void setStartLeft(int left)
    {
        float zoom = getZoom();
        if(-left * zoom > startLeftMinLimit && (-left + this.getWidth()) * zoom <  startLeftMaxLimit)
            startLeft = left;
        else if((startLeftMaxLimit - startLeftMinLimit) < this.getWidth() * zoom)
            startLeft =   (int)((((startLeftMaxLimit + startLeftMinLimit) / zoom)  + this.getWidth()) / 2);
        else if(-left * zoom <= startLeftMinLimit)
            startLeft = - (int)(startLeftMinLimit / zoom);
        else
            startLeft = - (int)(startLeftMaxLimit  / zoom) + this.getWidth();
    }

    private void setStartTop(int top)
    {
        float zoom = getZoom();
        if(-top * zoom > startTopMinLimit && (-top  + this.getHeight()) * zoom <  startTopMaxLimit)
            startTop = top;
        else if((startTopMaxLimit - startTopMinLimit) < this.getHeight() * zoom)
            startTop = (int)((((startTopMaxLimit + startTopMinLimit) / zoom) + this.getHeight()) / 2);
        else if(-top * zoom <= startTopMinLimit)
            startTop = - (int)(startTopMinLimit / zoom);
        else
            startTop = - (int)(startTopMaxLimit / zoom) + this.getHeight();
    }

    public void setMinZoomCount(int zoomCount)
    {
        if(zoomCount > this.zoomCount)
        {
            this.zoomCount = zoomCount;
            float zoom = (float)Math.pow(ZOOM_RATIO, zoomCount);
            chunkProvider.zoomChanged(zoom);
            updateChunkGroups();
        }
        minZoomCount = zoomCount;
    }

    public void setMaxZoomCount(int zoomCount)
    {
        if(zoomCount < this.zoomCount)
        {
            this.zoomCount = zoomCount;
            float zoom = (float)Math.pow(ZOOM_RATIO, zoomCount);
            chunkProvider.zoomChanged(zoom);
            updateChunkGroups();
        }
        maxZoomCount = zoomCount;
    }

    public void setStartLeftMinLimit(int min)
    {
        if(startLeftMaxLimit < min)
        {
            System.err.println("Error updating start left min");
            return;
        }

        startLeftMinLimit = min;
        setStartLeft(startLeft);
    }

    public void setStartLeftMaxLimit(int max)
    {
        if(max < startLeftMinLimit)
            return;

        startLeftMaxLimit = max;
        setStartLeft(startLeft);
    }

    public void setStartTopMinLimit(int min)
    {
        if(startTopMinLimit < min)
        return;

        startTopMinLimit = min;
        setStartTop(startTop);
    }

    public void setStartTopMaxLimit(int max)
    {
        if(max < startTopMaxLimit)
            return;
        startTopMaxLimit = max;
        setStartTop(startTop);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        verticalEdgeGroup.drawImage(g2d);
        horizontalEdgeGroup.drawImage(g2d);
        cornerGroup.drawImage(g2d);
        mainGroup.drawImage(g2d);
        g2d.setColor(new Color(0, 0, 0, 0.5F));
        float zoom = (float)Math.pow(ZOOM_RATIO, zoomCount);
        System.out.println(zoom);
        int rightBottomCornerX = (int)(startLeftMaxLimit / zoom + startLeft);
        int rightBottomCornerY = (int)(startTopMaxLimit / zoom + startTop);
        int leftTopCornerX = (int)(startLeftMinLimit / zoom + startLeft);
        int leftTopCornerY = (int)(startTopMinLimit / zoom + startTop);

        g2d.fillPolygon(
                new int[]{0, 0, leftTopCornerX, leftTopCornerX},
                new int[]{0, this.getHeight(), rightBottomCornerY, leftTopCornerY},
                4);
        g2d.fillPolygon(
                new int[]{0, this.getWidth(), rightBottomCornerX, leftTopCornerX},
                new int[]{0, 0, leftTopCornerY, leftTopCornerY},
                4);
        g2d.fillPolygon(
                new int[]{leftTopCornerX, 0, this.getWidth(), rightBottomCornerX},
                new int[]{rightBottomCornerY, this.getHeight(), this.getHeight(), rightBottomCornerY},
                4);
        g2d.fillPolygon(
                new int[]{rightBottomCornerX, this.getWidth(), this.getWidth(), rightBottomCornerX},
                new int[]{rightBottomCornerY, this.getHeight(), 0, leftTopCornerY},
                4);

        g2d.setColor(Color.WHITE);
        g2d.drawLine(mouseX, 0, mouseX, this.getHeight());
        g2d.drawLine(0, mouseY, this.getWidth(), mouseY);

        g2d.drawRect(
                leftTopCornerX,
                leftTopCornerY,
                rightBottomCornerX - leftTopCornerX,
                rightBottomCornerY - leftTopCornerY
        );

        g2d.drawLine(0, 0, leftTopCornerX, leftTopCornerY);
        g2d.drawLine(0, this.getHeight(), leftTopCornerX, rightBottomCornerY);
        g2d.drawLine(this.getWidth(), this.getHeight(), rightBottomCornerX, rightBottomCornerY);
        g2d.drawLine(this.getWidth(), 0, rightBottomCornerX, leftTopCornerY);

        g2d.drawString("(" + (int)((mouseX - startLeft) * zoom)   + ", " + (int)((mouseY - startTop) * zoom) + ")", mouseX + 20, mouseY + 20);
    }

    private void updateChunkGroups()
    {
//        if(startLeft)
        mainGroup.loadChunks(- startLeft / mainGroup.getChunkWidth(), - startTop / mainGroup.getChunkHeight(), true);

        if(startLeft < 0)
        {
//            System.out.println("Right");
            horizontalEdgeGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() + CHUNK_SIZE , - startTop / mainGroup.getChunkHeight(), true);
            if(startTop < 0)
                cornerGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() + CHUNK_SIZE , - startTop / mainGroup.getChunkHeight() + CHUNK_SIZE, true);
            else
                cornerGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() + CHUNK_SIZE , - startTop / mainGroup.getChunkHeight() - 1, true);
        }
        else{
//            System.out.println("Left");
            horizontalEdgeGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() - 1, - startTop / mainGroup.getChunkHeight(), true);
            if(startTop < 0)
                cornerGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() - 1 , - startTop / mainGroup.getChunkHeight() + CHUNK_SIZE, true);
            else
                cornerGroup.loadChunks(- startLeft / mainGroup.getChunkWidth() - 1 , - startTop / mainGroup.getChunkHeight() - 1, true);
        }

        if(startTop < 0)
        {
//            System.out.println("Bottom");
            verticalEdgeGroup.loadChunks(- startLeft / mainGroup.getChunkWidth(), - startTop / mainGroup.getChunkHeight() + CHUNK_SIZE, true);
        }
        else{
//            System.out.println("Top");
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

        setStartLeft(startLeft);
        setStartTop(startTop);

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

        setStartLeft(startLeft + diffX);
        setStartTop(startTop + diffY);

        updateChunkGroups();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
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
//        zoomCount -= e.getWheelRotation();
        if(setZoomCount(zoomCount - e.getWheelRotation()))
        {
            System.out.println(zoomCount);

            float zoom = (float)Math.pow(ZOOM_RATIO, zoomCount);

            setStartLeft((int)(mouseX - (mouseX - startLeft) * Math.pow(ZOOM_RATIO, e.getWheelRotation())));
            setStartTop((int)(mouseY - (mouseY - startTop) * Math.pow(ZOOM_RATIO, e.getWheelRotation())));
            chunkProvider.zoomChanged(zoom);

            updateChunkGroups();
        }
        else{
            setStartLeft((int)(mouseX - (mouseX - startLeft) * Math.pow(ZOOM_RATIO, e.getWheelRotation())));
            setStartTop((int)(mouseY - (mouseY - startTop) * Math.pow(ZOOM_RATIO, e.getWheelRotation())));
            repaint();
        }

    }
}
