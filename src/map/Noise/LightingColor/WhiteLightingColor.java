package map.Noise.LightingColor;

import java.awt.*;

public class WhiteLightingColor implements LightingColorPolicy{
    @Override
    public Color generateColor(float lightingAngle, float lightingAltitude) {
        return Color.WHITE;
    }
}
