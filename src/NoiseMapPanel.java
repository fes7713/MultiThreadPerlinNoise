import Noise.Array.LightingChanger;
import Noise.ChunkProvider;
import Noise.ColorProvider;
import Noise.NoiseChunkGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

public class NoiseMapPanel extends JPanel implements ComponentListener, MouseMotionListener, MouseListener, MouseWheelListener {
    private final ChunkProvider chunkProvider;
    private final ColorProvider colorProvider;
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

    private int zoomCount;

    private int maxZoomCount = DEFAULT_ZOOM_MAX_LIMIT;
    private int minZoomCount = DEFAULT_ZOOM_MIN_LIMIT;

    private int mouseX;
    private int mouseY;

    private static int DEFAULT_COLOR_LEVEL = 4096;
    private static final int CHUNK_SIZE = 5;
    private static final float ZOOM_RATIO = 2;

    private static final int DEFAULT_ZOOM_MAX_LIMIT = 4;
    private static final int DEFAULT_ZOOM_MIN_LIMIT = - 10;
    private static final int DEFAULT_CENTERX = 9000;
    private static final int DEFAULT_CENTERY = 0;
    private static final int DEFAULT_MAP_WIDTH = 4000;
    private static final int DEFAULT_MAP_HEIGHT = 4000;

    private float centerX;
    private float centerY;
    private float mapWidth;
    private float mapHeight;

    private final VariableChanger vc;
    private final MapEditor me;
//    private final Timer timer;
    public NoiseMapPanel()
    {
        tableWidth = tableHeight = CHUNK_SIZE;
        colorProvider = new ColorProvider(this::updateImage, DEFAULT_COLOR_LEVEL);
//        colorProvider.setPaintInterface(this::updateImage);
        chunkProvider = new ChunkProvider(colorProvider, this::repaint);
        mainGroup = new NoiseChunkGroup(chunkProvider, "Chunk",  100, 200,CHUNK_SIZE, CHUNK_SIZE);
        verticalEdgeGroup = new NoiseChunkGroup(chunkProvider, "Vertical Chunk",  100, 200,CHUNK_SIZE, 1);
        horizontalEdgeGroup = new NoiseChunkGroup(chunkProvider, "Horizontal Chunk",  100, 200,1, CHUNK_SIZE);
        cornerGroup = new NoiseChunkGroup(chunkProvider, "Horizontal Chunk",  100, 100,1, 1);

        mainGroup.loadChunks(0, 0, true);
        verticalEdgeGroup.loadChunks(0, 0, true);
        horizontalEdgeGroup.loadChunks(0, 0, true);
        cornerGroup.loadChunks(0, 0, true);
        addComponentListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);


        vc = new VariableChanger(this, chunkProvider, this::updateImage);
        vc.loadDefaultVariables(null);
        me = new MapEditor(this, this::repaint);
        me.loadDefaultMapSetting();

        zoomCount = 0;
        chunkProvider.setCenter((int)centerX, (int)centerY);
        setCenterX(centerX);
        setCenterY(centerY);
        moveCenter();


    }

    public void loadVariables(String fileName)
    {
        vc.loadVariable("variables", fileName, vc);
    }

    public void loadColorPreset(String fileName)
    {
        colorProvider.loadColorPreset(fileName);
    }

    public void loadMapSetting(String fileName)
    {
        me.loadMapSetting(fileName);
    }

    public void updateLighting()
    {
        mainGroup.updateLighting(this::updateImage);
        verticalEdgeGroup.updateLighting(this::updateImage);
        horizontalEdgeGroup.updateLighting(this::updateImage);
        cornerGroup.updateLighting(this::updateImage);
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
        vc.showVariableChanger();
    }

    public void showMapEditor(){
        me.showMapEditor();
    }

    public void showLightingChanger(){
        LightingChanger lc = new LightingChanger(chunkProvider, this::updateLighting);
        lc.showLightingChanger();
    }

    public void showColorEditor(){
        colorProvider.showColorEditor();
    }

    private float getZoom()
    {
        return (float)Math.pow(ZOOM_RATIO, zoomCount);
    }

    public int getColorLevel()
    {
        return colorProvider.getColorLevel();
    }

    public void setResolutionMin(int resolutionMin)
    {
        chunkProvider.setResolutionMin(resolutionMin);
    }

    public void setResolutionMax(int resolutionMax)
    {
        chunkProvider.setResolutionMax(resolutionMax);
    }

    public void setColorLevel(float colorLevel)
    {
        colorProvider.setColorLevel((int)colorLevel);
    }

    public void setColorLevel(int colorLevel)
    {
        colorProvider.setColorLevel(colorLevel);
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

    public int getStartLeft() {
        return startLeft;
    }

    public int getStartTop() {
        return startTop;
    }

    public void setStartLeft(int left)
    {
        float zoom = getZoom();
        if(-left * zoom > centerX - mapWidth / 2 && (-left + this.getWidth()) * zoom <  centerX + mapWidth / 2)
            startLeft = left;
        else if(mapWidth < this.getWidth() * zoom)
            startLeft = (int)(( - centerX / zoom  + this.getWidth() / 2));
        else if(-left * zoom <= centerX - mapWidth / 2)
            startLeft = - (int)((centerX - mapWidth / 2) / zoom);
        else
            startLeft = - (int)((centerX + mapWidth / 2)  / zoom) + this.getWidth();
    }

    public void setStartTop(int top)
    {
        float zoom = getZoom();
        if(-top * zoom > centerY - mapHeight / 2 && (-top  + this.getHeight()) * zoom <  centerY + mapHeight / 2)
            startTop = top;
        else if(mapHeight < this.getHeight() * zoom)
            startTop = (int)(( - centerY / zoom + this.getHeight() / 2));
        else if(-top * zoom <= centerY - mapHeight / 2)
            startTop = - (int)((centerY - mapHeight / 2) / zoom);
        else
            startTop = - (int)((centerY + mapHeight / 2) / zoom) + this.getHeight();
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

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }

    public void moveCenter()
    {
        setStartLeft((int)(( - centerX / getZoom()  + this.getWidth() / 2)));
        setStartTop((int)(( - centerY / getZoom() + this.getHeight() / 2)));
    }

    public void setCenterX(float centerX)
    {
        this.centerX = centerX;
//        setStartLeft((int)(( - centerX / getZoom()  + this.getWidth() / 2)));
        setStartLeft(startLeft);
        chunkProvider.setCenter(centerX, centerY);
        updateChunkGroups();
    }

    public void setCenterY(float centerY)
    {
        this.centerY = centerY;
//        setStartTop((int)(( - centerY / getZoom() + this.getHeight() / 2)));
        setStartTop(startTop);
        chunkProvider.setCenter(centerX, centerY);
        updateChunkGroups();
    }

    public void setMapWidth(float mapWidth)
    {
        if(mapWidth < 0)
            return;

        this.mapWidth = mapWidth;
        setStartTop(startTop);
    }

    public void setMapHeight(float mapHeight)
    {
        if(mapHeight < 0)
            return;
        this.mapHeight = mapHeight;
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

        int leftTopCornerX = (int)((centerX - mapWidth / 2) / zoom + startLeft);
        int leftTopCornerY = (int)((centerY - mapHeight / 2) / zoom + startTop);
        int rightBottomCornerX = (int)((centerX + mapWidth / 2) / zoom + startLeft);
        int rightBottomCornerY = (int)((centerY + mapHeight / 2) / zoom + startTop);

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

    public void clearChunks()
    {
        chunkProvider.clearMap(false);
    }

    public void updateChunkGroups()
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
                new NoiseChunkGroup(chunkProvider, "Main",  width, height,tableWidth, tableHeight);
        verticalEdgeGroup =
                new NoiseChunkGroup(chunkProvider, "Vertical",  width, mainGroup.getChunkHeight(),tableWidth, 1);
        horizontalEdgeGroup =
                new NoiseChunkGroup(chunkProvider, "Horizontal",  mainGroup.getChunkWidth(), height,1, tableHeight);
        cornerGroup =
                new NoiseChunkGroup(chunkProvider, "Corner",  mainGroup.getChunkWidth(), mainGroup.getChunkHeight(),1, 1);

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
            setStartLeft(startLeft);
            setStartTop(startTop);
            repaint();
        }
    }

    @Override
    public String toString()
    {
        return "CenterX," + centerX + "\n" +
                "CenterY," + centerY + "\n" +
                "Width," + mapWidth + "\n" +
                "Height," + mapHeight + "\n" +
                "Color," + colorProvider.getColorLevel();
    }

    public void fromString(String setting)
    {
        String[] split = setting
                .replace("CenterX,", "")
                .replace("CenterY,", "")
                .replace("Width,", "")
                .replace("Height,", "")
                .replace("Color,", "")
                .strip()
                .split("\n");
        if(split.length != 5)
            throw new RuntimeException("Illegal NoiseMapFile setting data");
        centerX = Float.parseFloat(split[0]);
        centerY = Float.parseFloat(split[1]);
        mapWidth = Float.parseFloat(split[2]);
        mapHeight = Float.parseFloat(split[3]);
        setColorLevel(Float.parseFloat(split[4]));
        chunkProvider.setCenter((int)centerX, (int)centerY);
    }
}