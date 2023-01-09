package Noise;

import java.util.HashMap;
import java.util.Map;

public class ChunkProvider {
    private final ColorProvider colorProvider;
    private final FastNoise fn;
    private final Map<Long, NoiseChunkInterface> loadedNoiseMap;
    private final ReusableChunkKeeper keeper;

    private int chunkWidth;
    private int chunkHeight;
    private float zoom;

    private float centerX;
    private float centerY;

    PaintInterface pi;

    private float NOISE_COEFFICIENT = 4F;
    private float NOISE_SHIFT = 0;
    private float NORMAL_COEFFICIENT = 0.03F;
    private float NORMAL_SHIFT = 125;

    private float MASK_SIZE = 20;
    private float MASK_SHADOW = 2F;

    private float LIGHTING_ANGLE = 0;
    private float LIGHTING_STRENGTH = 1;

    private float LIGHTING_X = LIGHTING_STRENGTH * (float)Math.cos(Math.toRadians(LIGHTING_ANGLE));
    private float LIGHTING_Y = LIGHTING_STRENGTH * (float)Math.sin(Math.toRadians(LIGHTING_ANGLE));
    private float LIGHTING_Z = -1;

    private int RESOLUTION_MIN = -2;
    private int RESOLUTION_MAX = 14;

    public ChunkProvider(ColorProvider colorProvider, PaintInterface pi)
    {
        this.colorProvider = colorProvider;
        this.pi = pi;

        zoom = 1;
        loadedNoiseMap = new HashMap<>();
        keeper = new ReusableChunkKeeper();

        fn = new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);
        chunkWidth = chunkHeight = 200;
    }

    public void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
    }

    public void preload(int left, int top, int width, int height)
    {
        // TODO
    }

    public void setCenter(float centerX, float centerY)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        synchronized (loadedNoiseMap)
        {
            for(NoiseChunkInterface chunk: loadedNoiseMap.values())
                chunk.setCenter(centerX, centerY);
        }
    }

    public NoiseChunkInterface requestNoiseChunk(int col, int row, boolean paintUpdate)
    {
        long key = NoiseChunkInterface.getChunkKey(col, row);

        if(loadedNoiseMap.containsKey(key))
        {
            return loadedNoiseMap.get(key);
        }
        else{
            NoiseChunkInterface noiseChunk;
            if(keeper.isEmpty())
                noiseChunk = new NoiseChunk("Chunk" + col + "-" + row, this, colorProvider, fn, col, row, chunkWidth, chunkHeight, zoom, centerX, centerY);
            else
            {
                noiseChunk = keeper.reuseChunk(col, row, zoom);
            }

            if(paintUpdate)
            {
                if(pi == null)
                    throw new RuntimeException("Need paint interface to be set before this method call");
                noiseChunk.updateChunk(pi);
            }
            else
                noiseChunk.updateChunk(null);
            synchronized (loadedNoiseMap)
            {
                loadedNoiseMap.put(key, noiseChunk);
            }
            return noiseChunk;
        }
    }

    public void clearMap(boolean reusable)
    {
        synchronized (loadedNoiseMap)
        {
            for(NoiseChunkInterface chunk: loadedNoiseMap.values()) {
                chunk.stopChunk();

            }
            keeper.clear();
            if(reusable)
                keeper.keepAllChunks(loadedNoiseMap);
            loadedNoiseMap.clear();
        }
    }

    public void dimensionChanged(int chunkWidth, int chunkHeight)
    {
        clearMap(false);
        this.chunkWidth = chunkWidth;
        this.chunkHeight = chunkHeight;
    }

    public void zoomChanged(float zoom)
    {
        clearMap(true);
        this.zoom = zoom;
    }

    public float getNoiseCoefficient(){
        return NOISE_COEFFICIENT;
    }

    public void setNoiseCoefficient(float coefficient)
    {
        NOISE_COEFFICIENT = coefficient;
    }

    public float getNoiseShift(){
        return NOISE_SHIFT;
    }

    public void setNoiseShift(float shift)
    {
        NOISE_SHIFT = shift;
    }

    public float getNormalCoefficient(){
        return NORMAL_COEFFICIENT;
    }

    public void setNormalCoefficient(float coefficient)
    {
        NORMAL_COEFFICIENT = coefficient;
    }

    public float getNormalShift(){
        return NORMAL_SHIFT;
    }

    public void setNormalShift(float shift)
    {
        NORMAL_SHIFT = shift;
    }

    public float getMaskSize()
    {
        return MASK_SIZE;
    }

    public void setMaskSize(float maskSize)
    {
        MASK_SIZE = maskSize;
    }

    public float getMaskShadow()
    {
        return MASK_SHADOW;
    }

    public void setMaskShadow(float maskShadow)
    {
        MASK_SHADOW = maskShadow;
    }

    public float getLightingAngle() {
        return LIGHTING_ANGLE;
    }

    public void setLightingAngle(float lightingAngle) {
        LIGHTING_ANGLE = lightingAngle;
        LIGHTING_X = LIGHTING_STRENGTH * (float)Math.cos(Math.toRadians(LIGHTING_ANGLE));
        LIGHTING_Y = LIGHTING_STRENGTH * (float)Math.sin(Math.toRadians(LIGHTING_ANGLE));
    }

    public float getLightingStrength() {
        return LIGHTING_STRENGTH;
    }

    public void setLightingStrength(float lightingStrength) {
        LIGHTING_STRENGTH = lightingStrength;
        LIGHTING_X = LIGHTING_STRENGTH * (float)Math.cos(Math.toRadians(LIGHTING_ANGLE));
        LIGHTING_Y = LIGHTING_STRENGTH * (float)Math.sin(Math.toRadians(LIGHTING_ANGLE));
    }

    public float getLightingX() {
        return LIGHTING_X;
    }

    public float getLightingY() {
        return LIGHTING_Y;
    }

    public float getLightingZ() {
        return LIGHTING_Z;
    }

    public int getResolutionMin() {
        return RESOLUTION_MIN;
    }

    public void setResolutionMin(int RESOLUTION_MIN) {
        this.RESOLUTION_MIN = RESOLUTION_MIN;
    }

    public int getResolutionMax() {
        return RESOLUTION_MAX;
    }

    public void setResolutionMax(int RESOLUTION_MAX) {
        this.RESOLUTION_MAX = RESOLUTION_MAX;
    }
}