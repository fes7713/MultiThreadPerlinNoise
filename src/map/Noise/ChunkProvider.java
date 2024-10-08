package map.Noise;

import map.Noise.LightingColor.LightingColorPolicy;
import map.Noise.LightingColor.NaturalTimeLightingColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ChunkProvider {
    private final ColorProvider colorProvider;
    private final FastNoise fn;
    private final Map<Long, NoiseChunkInterface> loadedNoiseMap;
    private final ReusableChunkKeeper keeper;

    private int chunkWidth;
    private int chunkHeight;

    private int arrayWidth;
    private int arrayHeight;

    private final float widthArrayDivider;
    private final float heightArrayDivider;

    private float zoom;

    private float centerX;
    private float centerY;

    PaintInterface pi;

    private float NOISE_COEFFICIENT = 4F;
    private float NOISE_SHIFT = 0;
    private float NORMAL_COEFFICIENT = 0.03F;
    private float NORMAL_SHIFT = 0;

    private float MASK_SIZE = 20;
    private float MASK_SHADOW = 2F;

    private float LIGHTING_ANGLE = 90;
    private float LIGHTING_ALTITUDE = 45;
    private float LIGHTING_STRENGTH = 1.7F;

    private float LIGHTING_X;
    private float LIGHTING_Y;
    private float LIGHTING_Z;

    private LightingColorPolicy lightingColorPolicy;
    private Color lightingColor;

    private int SPECULAR_BRIGHTNESS = 400;
    private int SPECULAR_INTENSITY = 10;

    private float AMBIENT_INTENSITY = 0.3F;

    private int RESOLUTION_MIN = -4;
    private int RESOLUTION_MAX = 10;

    public ChunkProvider(ColorProvider colorProvider, PaintInterface pi)
    {
        this(colorProvider, 1, 1, pi);
    }

    public ChunkProvider(ColorProvider colorProvider, float widthArrayDivider, float heightArrayDivider, PaintInterface pi)
    {
        this.colorProvider = colorProvider;
        this.widthArrayDivider = widthArrayDivider;
        this.heightArrayDivider = heightArrayDivider;
        this.pi = pi;
        zoom = 1;
        loadedNoiseMap = new HashMap<>();
        keeper = new ReusableChunkKeeper();

        fn = new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.CubicFractal);
        fn.SetInterp(FastNoise.Interp.Quintic);
        chunkWidth = chunkHeight = 200;

        arrayWidth = (int)(chunkWidth / widthArrayDivider);
        arrayHeight = (int)(chunkHeight / heightArrayDivider);
        updateLighting();
        lightingColorPolicy = new NaturalTimeLightingColor();
        lightingColor = lightingColorPolicy.generateColor(LIGHTING_ANGLE, LIGHTING_ALTITUDE);
    }

    public void setPaintInterface(PaintInterface pi)
    {
        this.pi = pi;
    }

    public void preload(int left, int top, int width, int height)
    {
        // TODO
    }

    public int getLoadedChunkSize()
    {
        return loadedNoiseMap.size();
    }

    public void setCenter(float centerX, float centerY)
    {
        this.centerX = centerX;
        this.centerY = centerY;
        synchronized (loadedNoiseMap)
        {
            for(NoiseChunkInterface chunk: loadedNoiseMap.values())
            {
                chunk.setCenter(centerX, centerY);
                chunk.updateImage(pi);
            }
        }
    }

    public void applyVariableChange()
    {
        synchronized (loadedNoiseMap)
        {
            for(NoiseChunkInterface chunk: loadedNoiseMap.values())
                chunk.variableChanged();
        }
    }

    public synchronized NoiseChunkInterface requestNoiseChunk(int col, int row, boolean paintUpdate)
    {
        long key = NoiseChunkInterface.getChunkKey(col, row);
        if(loadedNoiseMap.containsKey(key))
        {
            return loadedNoiseMap.get(key);
        }
        else{
            NoiseChunkInterface noiseChunk;
            if(keeper.isEmpty())
                noiseChunk = new NoiseChunk(this, colorProvider, fn, col, row, chunkWidth, chunkHeight, zoom, centerX, centerY, arrayWidth, arrayHeight);
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

        arrayWidth = (int)(chunkWidth / widthArrayDivider);
        arrayHeight = (int)(chunkHeight / heightArrayDivider);
    }

    public void zoomChanged(float zoom)
    {
        synchronized(this)
        {
            clearMap(true);
            this.zoom = zoom;
        }
    }

    private void updateLighting()
    {
        LIGHTING_X = -(float)(LIGHTING_STRENGTH * Math.cos(Math.toRadians(LIGHTING_ANGLE)) * Math.cos(Math.toRadians(LIGHTING_ALTITUDE)));
        LIGHTING_Y = -(float)(LIGHTING_STRENGTH * Math.sin(Math.toRadians(LIGHTING_ANGLE)) * Math.cos(Math.toRadians(LIGHTING_ALTITUDE)));
        LIGHTING_Z = -(float)(LIGHTING_STRENGTH * Math.sin(Math.toRadians(LIGHTING_ALTITUDE)));
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

    public static float pow(float p, int n)
    {
        float a = 1;
        for(int i = 0; i < n; i++)
            a *= p;
        return a;
    }

    public void setLightingAngle(float lightingAngle) {
        LIGHTING_ANGLE = lightingAngle;
        updateLighting();
        lightingColor = lightingColorPolicy.generateColor(LIGHTING_ANGLE, LIGHTING_ALTITUDE);
    }

    public float getLightingAltitude()
    {
        return LIGHTING_ALTITUDE;
    }

    public void setLightingAltitude(float lightingAltitude)
    {
        LIGHTING_ALTITUDE = lightingAltitude;
        updateLighting();
    }

    public float getLightingStrength() {
        return LIGHTING_STRENGTH;
    }

    public void setLightingStrength(float lightingStrength) {
        LIGHTING_STRENGTH = lightingStrength;
        updateLighting();
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

    public Color getLightingColor() {
        return lightingColor;
    }

    public void setLightingColorPolicy(LightingColorPolicy policy)
    {
        lightingColorPolicy = policy;
        lightingColor = lightingColorPolicy.generateColor(LIGHTING_ANGLE, LIGHTING_ALTITUDE);
    }

    public int getSpecularBrightness()
    {
        return SPECULAR_BRIGHTNESS;
    }

    public void setSpecularBrightness(float brightness)
    {
        SPECULAR_BRIGHTNESS = (int)brightness;
    }

    public int getSpecularIntensity()
    {
        return SPECULAR_INTENSITY;
    }

    public void setSpecularIntensity(float intensity)
    {
        SPECULAR_INTENSITY = (int)intensity;
    }

    public float getAmbientIntensity()
    {
        return AMBIENT_INTENSITY;
    }

    public void setAmbientIntensity(float intensity)
    {
        AMBIENT_INTENSITY = intensity;
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

    public float getWidthArrayDivider(){
        return widthArrayDivider;
    }

    public float getHeightArrayDivider(){
        return heightArrayDivider;
    }
}