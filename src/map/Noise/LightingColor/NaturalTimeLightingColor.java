package map.Noise.LightingColor;

import java.awt.*;

import static map.Noise.ChunkProvider.pow;

public class NaturalTimeLightingColor implements LightingColorPolicy{
    @Override
    public Color generateColor(float lightingAngle, float lightingAltitude) {
        int a = 115;
        double c = lightingAngle / a - Math.PI / 4;
        float red_in = 0.97F - pow((float)Math.cos(c), 6) / 0.993F;
        float RED = 1 - pow(pow(pow(red_in, 2), 2), 2);
        float green_in = 0.96F - pow((float)Math.cos(c), 6) / 2.29F;
        float GREEN = 1 - pow(pow(green_in, 3), 3);
        float blue_in = 0.994F - pow((float)Math.cos(c), 8) / 20F;
        float BLUE = 1 - pow(pow(pow(pow(blue_in, 3), 3), 3), 3);
        return new Color(RED, GREEN, BLUE);
    }
}
