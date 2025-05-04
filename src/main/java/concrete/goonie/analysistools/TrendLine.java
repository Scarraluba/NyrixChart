package concrete.goonie.analysistools;


import concrete.goonie.ChartConfig;
import concrete.goonie.enums.ENUM_TIMEFRAME;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.time.LocalDateTime;

public class TrendLine extends Tool {
    protected final Line2D.Double line;

    public TrendLine(ChartConfig config, double price1, LocalDateTime dateTime1,
                     double price2, LocalDateTime dateTime2) {
        super(config, price1, dateTime1, price2, dateTime2, ENUM_TIMEFRAME.PERIOD_H1);
        this.line = new Line2D.Double();
    }

    @Override
    public double calculateArea() {
        return 0;
    }

    @Override
    public boolean overlapsWith(Tool other) {
        if (other instanceof TrendLine) {
            TrendLine otherLine = (TrendLine) other;
            // Check if the y-coordinates are the same and x-coordinates overlap
            return line.getY1() == otherLine.line.getY1() &&
                    line.intersectsLine(otherLine.line);
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        double x1 = timeToXCoordinate(dateTime1);
        double x2 = timeToXCoordinate(dateTime2);

        Point2D transformedStart = transform.transform(new Point2D.Double(x1, price1), null);
        Point2D transformedEnd = transform.transform(new Point2D.Double(x2, price2), null);

        // Draw the line
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth));
        line.setLine(transformedStart, transformedEnd);
        g2d.draw(line);

    }

    private void drawHandles(Graphics2D g2d, AffineTransform transform, int width) {
        // Transform the start and end points for the handles
        Point2D transformedStart = transform.transform(new Point2D.Double(line.getX1(), line.getY1()), null);
        Point2D transformedEnd = transform.transform(new Point2D.Double(line.getX2(), line.getY2()), null);

        // Draw handles as small circles at the start and end points of the line
        g2d.setColor(new Color(0x2B89A6));
        g2d.setStroke(new BasicStroke(strokeWidth + 2));
        Line2D.Double transformedLine = new Line2D.Double(new Point2D.Double(0, transformedStart.getY()), new Point2D.Double(width, transformedEnd.getY()));


        g2d.draw(transformedLine);
        drawHandle(g2d, new Point2D.Double(0, transformedStart.getY()));
        drawHandle(g2d, new Point2D.Double(width, transformedEnd.getY()));
    }

    private void drawHandle(Graphics2D g2d, Point2D point) {
        g2d.fillOval((int) point.getX() - HANDLE_SIZE / 2, (int) point.getY() - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE);
    }

    @Override
    protected void move(double dx, double dy) {

    }
}
