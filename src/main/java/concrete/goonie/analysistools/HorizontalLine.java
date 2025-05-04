package concrete.goonie.analysistools;


import concrete.goonie.ChartConfig;
import concrete.goonie.enums.ENUM_TIMEFRAME;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class HorizontalLine  extends Tool {
    protected final Line2D.Double line;

    public HorizontalLine(ChartConfig config, double price1) {
        super( config,price1, null, ENUM_TIMEFRAME.PERIOD_H1);
        this.line = new Line2D.Double(0, price1, 0, price1);
    }

    public HorizontalLine(double price1, Color color, boolean isDraggable, boolean isDotted, String s) {
        this(null,price1);
        setColor(color);
        setDotted(isDotted);
        setDraggable(isDraggable);
    }

    @Override
    public double calculateArea() {
        return 0;
    }

    @Override
    public boolean overlapsWith(Tool other) {
        if (other instanceof HorizontalLine ) {
            HorizontalLine otherLine = (HorizontalLine) other;
            // Check if the y-coordinates are the same and x-coordinates overlap
            return line.getY1() == otherLine.line.getY1() &&
                    line.intersectsLine(otherLine.line);
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        Point2D transformedStart = transform.transform(new Point2D.Double(0, line.getY1()), null);
        int xx = width - config.getyPad();   Point2D transformedEnd = transform.transform(new Point2D.Double(xx, line.getY2()), null);

        // Create a new transformed line for drawing
        Line2D.Double transformedLine = new Line2D.Double(new Point2D.Double(0, transformedStart.getY()), new Point2D.Double(xx, transformedEnd.getY()));

        if (isHovered) drawHandles(g2d, transform, xx);

        g2d.setColor(color);

        if (isDotted) {
            float[] dashPattern = {5f, 5f};  // Dash pattern (dotted)
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dashPattern, 0f));
        } else {
            g2d.setStroke(new BasicStroke(strokeWidth));  // Solid line
        }
        Point2D transformed = transform.transform(new Point2D.Double(xx, line.getY2()), null);

        g2d.drawString(text, 8, (int) transformed.getY() - 5);
        g2d.draw(transformedLine);
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

    @Override
    public void setPrice1(double price1) {
        super.setPrice1(price1);
        line.setLine(line.x1, price1, line.x2, price1);
    }
}
