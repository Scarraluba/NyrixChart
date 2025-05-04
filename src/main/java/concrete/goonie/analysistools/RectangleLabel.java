package concrete.goonie.analysistools;

import concrete.goonie.ChartConfig;
import concrete.goonie.enums.ENUM_TIMEFRAME;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.LocalDateTime;

public class RectangleLabel extends Tool {

    public RectangleLabel(ChartConfig config, double price1, LocalDateTime dateTime1, double price2, LocalDateTime dateTime2, ENUM_TIMEFRAME timeframe) {
        super(config, price1, dateTime1, price2, dateTime2, timeframe);
    }

    @Override
    public double calculateArea() {
        Duration duration = Duration.between(dateTime1, dateTime2);
        double width = (double) duration.toSeconds() / timeframe.getDuration().toSeconds();
        double height = Math.abs(price2 - price1);
        return width * height;
    }

    @Override
    public boolean overlapsWith(Tool other) {

        if (!(other instanceof RectangleLabel)) {
            return false;
        }

        RectangleLabel otherRect = (RectangleLabel) other;
        boolean timeOverlap = !dateTime1.isAfter(otherRect.dateTime2) &&
                !dateTime2.isBefore(otherRect.dateTime1);
        double thisMinY = Math.min(price1, price2);
        double thisMaxY = Math.max(price1, price2);
        double otherMinY = Math.min(otherRect.price1, otherRect.price2);
        double otherMaxY = Math.max(otherRect.price1, otherRect.price2);
        boolean priceOverlap = thisMaxY >= otherMinY && thisMinY <= otherMaxY;

        return timeOverlap && priceOverlap;
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        int visibleCandleCount = (int) (width / transform.getScaleX());
        int barWidth = Math.max(1, (int) (width / (visibleCandleCount * 1.1))); // 1.2 provides some spacing

        double x1 = timeToXCoordinate(dateTime1);
        double x2 = timeToXCoordinate(dateTime2);

        // Determine bounds before transformation
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(price1, price2);
        double maxY = Math.max(price1, price2);

        // Transform to screen space
        Point2D p1 = transform.transform(new Point2D.Double(minX, maxY), null); // bottom-left
        Point2D p2 = transform.transform(new Point2D.Double(maxX, minY), null); // top-right

        double rectX = Math.min(p1.getX(), p2.getX());
        double rectY = Math.min(p1.getY(), p2.getY());
        double rectWidth = Math.abs(p2.getX() - p1.getX());
        double rectHeight = Math.abs(p2.getY() - p1.getY());

        // Check if the rectangle is within the visible bounds
        boolean isVisible = rectX + rectWidth >= 0 &&
                rectX <= width &&
                rectY + rectHeight >= 0 &&
                rectY <= height;

        if (isVisible) {
            frameRect.setRoundRect(rectX - ((double) barWidth / 2), rectY, rectWidth + ((double) barWidth), rectHeight, radius, radius);
            // System.out.println(rectX);
            // Draw the rectangle
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(this.strokeWidth));
            //g2d.draw(frameRect);

            g2d.setColor(shapeFill);
           // g2d.fill(frameRect);

            // Draw text if available
            if (text != null && !text.isEmpty()) {
                drawCenteredText(g2d, rectX, rectY, rectWidth, rectHeight);
            }
        }
    }

    private void drawCenteredText(Graphics2D g2d, double x, double y, double width, double height) {
        // Set text color and font
        g2d.setColor(color);
        g2d.setFont(textFont);

        // Calculate text position
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();

        // Calculate center position
        double centerX = x + (width - textWidth) / 2;
        double centerY = y + (height + textHeight / 2) / 2;

        // Draw the text
        g2d.drawString(text, (float) centerX, (float) centerY);
    }

    @Override
    protected void move(double dx, double dy) {
        Duration timeDelta = timeframe.getDuration().multipliedBy((long) dx);
        dateTime1 = dateTime1.plus(timeDelta);
        dateTime2 = dateTime2.plus(timeDelta);
        price1 += dy;
        price2 += dy;
    }

    public double getFrameRectX() {
        return frameRect.getX();
    }

    public double getFrameRectY() {
        return frameRect.getY();
    }

    public double getFrameRectWidth() {
        return frameRect.getWidth();
    }

    public double getFrameRectHeight() {
        return frameRect.getHeight();
    }
}
