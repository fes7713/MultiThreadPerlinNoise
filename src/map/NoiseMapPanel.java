package map;

import map.Cursor.CoordinateCursorGraphics;
import map.Cursor.CursorGraphics;
import map.Cursor.EmptyCursorGraphics;
import map.Noise.Array.LightingChanger;
import map.Noise.ChunkProvider;
import map.Noise.ColorProvider;
import map.Noise.LightingColor.NaturalTimeLightingColor;
import map.Noise.LightingColor.WhiteLightingColor;
import map.Noise.NoiseChunkGroup;
import map.Noise.PaintInterface;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.stream.Stream;

public class NoiseMapPanel extends JPanel implements ComponentListener, MouseMotionListener, MouseListener, MouseWheelListener {
    @NotNull
    private final ChunkProvider chunkProvider;
    @NotNull
    private final ColorProvider colorProvider;
    @NotNull
    private final NoiseChunkGroup mainGroup;
    @NotNull
    private final NoiseChunkGroup verticalEdgeGroup;
    @NotNull
    private final NoiseChunkGroup horizontalEdgeGroup;
    @NotNull
    private final NoiseChunkGroup cornerGroup;

    @NotNull
    private CursorGraphics cursorGraphics;

    @NotNull
    private final VariableChanger vc;
    @NotNull
    private final MapEditor me;

    private int startX;
    private int startY;
    private float startLeft;
    private float startTop;

    private final int tableWidth;
    private final int tableHeight;

    private int zoomCount;

    private int maxZoomCount = DEFAULT_ZOOM_MAX_LIMIT;
    private int minZoomCount = DEFAULT_ZOOM_MIN_LIMIT;

    private int mouseX;
    private int mouseY;

    private static final int DEFAULT_COLOR_LEVEL = 256;
    private static final int CHUNK_SIZE = 5;
    private static final float ZOOM_RATIO = 2;

    private static final int DEFAULT_ZOOM_MAX_LIMIT = 4;
    private static final int DEFAULT_ZOOM_MIN_LIMIT = - 10;
    private static final int DEFAULT_CENTERX = 9000;
    private static final int DEFAULT_CENTERY = 0;
    private static final int DEFAULT_MAP_WIDTH = 4000;
    private static final int DEFAULT_MAP_HEIGHT = 4000;

    private static final int CANVAS_WIDTH = 100;
    private static final int CANVAS_HEIGHT = 200;

    private float centerX;
    private float centerY;
    private float mapWidth;
    private float mapHeight;

    private float maxSunAltitude = 55;

    public NoiseMapPanel()
    {
        this(1, 1);
    }

    public NoiseMapPanel(float widthDivider, float heightDivider)
    {
        tableWidth = tableHeight = CHUNK_SIZE;
        colorProvider = new ColorProvider(this::updateImage, DEFAULT_COLOR_LEVEL);
//        colorProvider.setPaintInterface(this::updateImage);
        chunkProvider = new ChunkProvider(colorProvider, widthDivider, heightDivider, this::repaint);
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


        vc = new VariableChanger(chunkProvider, this::updateImage);
        vc.loadDefaultVariables(vc);
        me = new MapEditor(this, this::repaint);
        me.loadDefaultMapSetting();

        zoomCount = 0;
        chunkProvider.setCenter((int)centerX, (int)centerY);
        setCenterX(centerX);
        setCenterY(centerY);
        moveCenter();

        cursorGraphics = new EmptyCursorGraphics() {};
    }

    public void loadVariables(String fileName)
    {
        vc.loadVariable("variables", fileName, true);
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
        updateLighting(true);
    }

    public void updateLighting(boolean update)
    {
        PaintInterface pi = null;
        if(update)
            pi = this::updateImage;

        mainGroup.updateLighting(pi);
        verticalEdgeGroup.updateLighting(pi);
        horizontalEdgeGroup.updateLighting(pi);
        cornerGroup.updateLighting(pi);
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

    public float getZoom()
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

    public float getStartLeft() {
        return startLeft;
    }

    public float getStartTop() {
        return startTop;
    }

    public void setStartLeft(float left)
    {
        float zoom = getZoom();
        if(-left > centerX - mapWidth / 2 && (-left + this.getWidth() * zoom )<  centerX + mapWidth / 2)
            startLeft = left;
        else if(mapWidth < this.getWidth() * zoom)
            startLeft = (int)(( - centerX  + this.getWidth() / 2 * zoom));
        else if(-left <= centerX - mapWidth / 2)
            startLeft = - (int)((centerX - mapWidth / 2));
        else
            startLeft = - (int)(centerX + mapWidth / 2 - this.getWidth() * zoom);
    }

    public void setStartTop(float top)
    {
        float zoom = getZoom();
        if(-top > centerY - mapHeight / 2 && (-top  + this.getHeight() * zoom) <  centerY + mapHeight / 2)
            startTop = top;
        else if(mapHeight < this.getHeight() * zoom)
            startTop = (int)(( - centerY + this.getHeight() / 2 * zoom));
        else if(-top <= centerY - mapHeight / 2)
            startTop = - (int)((centerY - mapHeight / 2));
        else
            startTop = - (int)(centerY + mapHeight / 2 - this.getHeight() * zoom);
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
        setStartLeft((int)(( - centerX + this.getWidth() / 2 * getZoom())));
        setStartTop((int)(( - centerY + this.getHeight() / 2 * getZoom())));
    }

    public void setCenter(float centerX, float centerY)
    {
        this.centerX = centerX;
        this.centerY = centerY;

        setStartLeft(startLeft);
        setStartTop(startTop);
        chunkProvider.setCenter(centerX, centerY);
        updateChunkGroups();
    }

    public void setCenterX(float centerX)
    {
//        System.out.println(centerX);
//        System.out.println(startLeft);
        this.centerX = centerX;
        setStartLeft(startLeft);
        chunkProvider.setCenter(centerX, centerY);
        updateChunkGroups();
    }

    public void setCenterY(float centerY)
    {
        this.centerY = centerY;
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

    public void hideCursorGraphics() {
        this.cursorGraphics = new EmptyCursorGraphics() {};
    }

    public void showCursorGraphics() {
        this.cursorGraphics = new CoordinateCursorGraphics() {};
    }

    public void setCursorGraphics(@NotNull CursorGraphics cursorGraphics){
        this.cursorGraphics = cursorGraphics;
    }

    public void setWhiteLightingColor()
    {
        chunkProvider.setLightingColorPolicy(new WhiteLightingColor());
    }

    public void setNaturalLightingColor()
    {
        chunkProvider.setLightingColorPolicy(new NaturalTimeLightingColor());
    }

    public void setMaxSunAltitude(float maxSunAltitude) {
        this.maxSunAltitude = maxSunAltitude;
    }

    public void setLightingTime(float hour_in_24, float minute)
    {
        float time = hour_in_24 % 24 + (minute % 60) / 60;
        chunkProvider.setLightingAltitude((float)(maxSunAltitude * Math.sin(time * Math.PI / 12 - Math.PI / 2)));
        chunkProvider.setLightingAngle(15 * (time - 12) - 90);
        updateLighting(false);
        updateImage();
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

        int leftTopCornerX = (int)((centerX - mapWidth / 2 + startLeft) / zoom);
        int leftTopCornerY = (int)((centerY - mapHeight / 2 + startTop) / zoom);
        int rightBottomCornerX = (int)((centerX + mapWidth / 2 + startLeft) / zoom);
        int rightBottomCornerY = (int)((centerY + mapHeight / 2 + startTop) / zoom);

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

        cursorGraphics.drawCursor(g2d, mouseX, mouseY, (int)getGameX(mouseX), (int)getGameY(mouseY));
    }

    public void clearChunks()
    {
        chunkProvider.clearMap(false);
    }

    public void updateChunkGroups()
    {
        float zoom = getZoom();

        int chunkX = (int)(- startLeft / zoom / mainGroup.getChunkWidth());
        int chunkY = (int)(- startTop / zoom / mainGroup.getChunkHeight());
//        if(startLeft)
        mainGroup.loadChunks(chunkX, chunkY, true);

        if(startLeft < 0)
        {
//            System.out.println("Right");
            horizontalEdgeGroup.loadChunks(chunkX + CHUNK_SIZE , chunkY, true);
            if(startTop < 0)
                cornerGroup.loadChunks(chunkX + CHUNK_SIZE , chunkY + CHUNK_SIZE, true);
            else
                cornerGroup.loadChunks(chunkX + CHUNK_SIZE , chunkY - 1, true);
        }
        else{
//            System.out.println("Left");
            horizontalEdgeGroup.loadChunks(chunkX - 1, chunkY, true);
            if(startTop < 0)
                cornerGroup.loadChunks(chunkX - 1 , chunkY + CHUNK_SIZE, true);
            else
                cornerGroup.loadChunks(chunkX - 1 , chunkY - 1, true);
        }

        if(startTop < 0)
        {
//            System.out.println("Bottom");
            verticalEdgeGroup.loadChunks(chunkX, chunkY + CHUNK_SIZE, true);
        }
        else{
//            System.out.println("Top");
            verticalEdgeGroup.loadChunks(chunkX, chunkY -1, true);
        }

        Stream.of(mainGroup, horizontalEdgeGroup, verticalEdgeGroup, cornerGroup)
                .forEach(group -> {
                    group.setChunkShiftX((int)(startLeft / zoom) / mainGroup.getChunkWidth());
                    group.setChunkShiftY((int)(startTop / zoom) / mainGroup.getChunkHeight());
                    group.setPixelShiftX((int)(startLeft / zoom) % mainGroup.getChunkWidth());
                    group.setPixelShiftY((int)(startTop / zoom) % mainGroup.getChunkHeight());
                });

        repaint();
    }

    public float getGameX(int screenX)
    {
        return screenX * getZoom() - startLeft;
    }

    public float getGameY(int screenY)
    {
        return screenY * getZoom() - startTop;
    }

    public float getGameSize(int screenSize)
    {
        return screenSize * getZoom();
    }

    public int getScreenX(float gameX)
    {
        return (int)((gameX + startLeft) / getZoom());
    }

    public int getScreenY(float gameY)
    {
        return (int)((gameY + startTop) / getZoom());
    }

    public int getScreenSize(float gameSize)
    {
        return (int)(gameSize / getZoom());
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int width = e.getComponent().getWidth();
        int height = e.getComponent().getHeight();

        setStartLeft(startLeft);
        setStartTop(startTop);

        chunkProvider.dimensionChanged((int)Math.ceil(width / (double)tableWidth), (int)Math.ceil(height / (double)tableHeight));

        mainGroup.setCanvasDimension(width, height);
        verticalEdgeGroup.setCanvasDimension(width, mainGroup.getChunkHeight());
        horizontalEdgeGroup.setCanvasDimension(mainGroup.getChunkWidth(), height);
        cornerGroup.setCanvasDimension(mainGroup.getChunkWidth(), mainGroup.getChunkHeight());

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

        float zoom = getZoom();
        setStartLeft(startLeft + diffX * zoom);
        setStartTop(startTop + diffY * zoom);

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
        float pre = (float)Math.pow(ZOOM_RATIO, zoomCount);
        if(setZoomCount(zoomCount + e.getWheelRotation()))
        {
            float zoom = (float)Math.pow(ZOOM_RATIO, zoomCount);

//            setStartLeft((int)(mouseX * zoom * (1 - Math.pow(ZOOM_RATIO, e.getWheelRotation()))) + startLeft);
//            setStartTop((int)(mouseY  * zoom * (1 - Math.pow(ZOOM_RATIO, e.getWheelRotation()))) + startTop);
            setStartLeft((int)(mouseX * (zoom - pre)) + startLeft);
            setStartTop((int)(mouseY * (zoom - pre)) + startTop);
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