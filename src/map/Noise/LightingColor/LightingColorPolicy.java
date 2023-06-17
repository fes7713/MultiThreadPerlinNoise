package map.Noise.LightingColor;

import java.awt.*;

public interface LightingColorPolicy {
    Color generateColor(float lightingAngle, float lightingAltitude);
}
