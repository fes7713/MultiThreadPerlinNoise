package map.Cursor;

import java.awt.*;

public interface CoordinateCursorGraphics extends CursorGraphics{
    @Override
    default void drawCursor(Graphics2D g2d, int left, int top, float zoom) {

    }
}
