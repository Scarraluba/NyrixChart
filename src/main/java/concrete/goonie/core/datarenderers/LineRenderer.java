package concrete.goonie.core.datarenderers;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;
import concrete.goonie.datatypes.LineData;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class LineRenderer extends Renderer {
    private final List<LineData> lineDataList = new CopyOnWriteArrayList<>();
    private int width = 0, height = 0;
    private AffineTransform transform;

    public LineRenderer(ChartConfig config) {
        super(config);
    }

    public void addLineData(LineData lineData) {
        Objects.requireNonNull(lineData, "LineData cannot be null");
        lineDataList.add(lineData);
    }

    public void addPoint(Point2D point, int lineIndex) {
        if (lineIndex >= 0 && lineIndex < lineDataList.size()) {
            lineDataList.get(lineIndex).addPoint(point);
        }
    }

    @Override
    protected boolean contains(Point2D point) {
        for (LineData lineData : lineDataList) {
            List<Point2D> points = lineData.getPoints();
            for (int i = 1; i < points.size(); i++) {
                Point2D start = transform.transform(points.get(i - 1), null);
                Point2D end = transform.transform(points.get(i), null);

                if (isPointNearLine(start, end, point, lineData.getWidth())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPointNearLine(Point2D start, Point2D end, Point2D point, float lineWidth) {
        double tolerance = Math.max(5, lineWidth * 1.5); // Dynamic tolerance based on line width
        Line2D.Double line = new Line2D.Double(start, end);
        return line.ptSegDist(point) <= tolerance;
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        this.width = width;
        this.height = height;
        this.transform = transform;

        for (LineData lineData : lineDataList) {
            drawLineData(g2d, lineData);
        }
    }

    private void drawLineData(Graphics2D g2d, LineData lineData) {
        List<Point2D> points = lineData.getPoints();
        if (points.size() < 2) return;

        // Set line style properties
        g2d.setColor(lineData.getColor());
//        g2d.setStroke(new BasicStroke(
//                lineData.getWidth(),
//                lineData.getStrokeCap(),
//                lineData.getStrokeJoin(),
//                10.0f,
//                lineData.getDashPattern(),
//                0.0f
//        ));

        Point2D previousPoint = null;
        for (Point2D point : points) {
            Point2D transformedPoint = transform.transform(point, null);

            if (previousPoint != null) {
                if (isWithinBounds(transformedPoint)) {
                    g2d.draw(new Line2D.Double(previousPoint, transformedPoint));
                }
            }
            previousPoint = transformedPoint;
        }
    }

    private boolean isWithinBounds(Point2D point) {
        double x = point.getX();
        return x >= 0 && x <= width;
    }

    @Override
    protected void move(double dx, double dy) {
        // Move all points in all line data sets
        for (LineData lineData : lineDataList) {
            for (Point2D point : lineData.getPoints()) {
                point.setLocation(point.getX() + dx, point.getY() + dy);
            }
        }
    }

    public void clearAllLines() {
        lineDataList.clear();
    }

    public void removeLineData(int index) {
        if (index >= 0 && index < lineDataList.size()) {
            lineDataList.remove(index);
        }
    }
}