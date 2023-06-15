package map.Cursor;

import java.awt.*;

public interface EmptyCursorGraphics extends CursorGraphics{
    @Override
    default void drawCursor(Graphics2D g2d, int screenX, int screenY, int gameX, int gameY)  {

    }
}
