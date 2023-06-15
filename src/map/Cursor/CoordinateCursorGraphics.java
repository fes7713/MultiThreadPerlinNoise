package map.Cursor;

import java.awt.*;

public interface CoordinateCursorGraphics extends CursorGraphics{
    @Override
    default void drawCursor(Graphics2D g2d, int screenX, int screenY, int gameX, int gameY) {
        int width = g2d.getClipBounds().width;
        int height = g2d.getClipBounds().height;

        String str = String.format("(%d, %d)", gameX, gameY);

        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.drawLine(0, screenY - 1, width, screenY - 1);
        g2d.drawLine(0, screenY + 1, width, screenY + 1);
        g2d.drawLine(screenX - 1, 0, screenX - 1, height);
        g2d.drawLine(screenX + 1, 0, screenX + 1, height);

        Rectangle strRect = g2d.getFontMetrics().getStringBounds(str, g2d).getBounds();
        g2d.fillRect(screenX, screenY - strRect.height, strRect.width, strRect.height);

        g2d.setColor(Color.WHITE);
        g2d.drawLine(0, screenY, width, screenY);
        g2d.drawLine(screenX, 0, screenX, height);
        g2d.drawString(str, screenX, screenY);
    }
}
