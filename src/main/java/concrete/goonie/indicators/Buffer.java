package concrete.goonie.indicators;


import concrete.goonie.ChartConfig;
import concrete.goonie.analysistools.RectangleLabel;
import concrete.goonie.core.Renderer;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class Buffer extends Renderer {
    private final List<Double> points = new ArrayList<>();
    private final ArrayList<RectangleLabel> bufferBox = new ArrayList<>();

    private Color color = Color.BLUE;
    private float width = 1.0f;
    private int arrowSize = 5;

    private final String label;
    private final BufferType type;

    public Buffer(ChartConfig config, BufferType type, String label) {
        super(config);
        this.type = type;
        this.label = label;
    }

    public void addPoint(double point) {
        points.add(point);
    }

    public void addPoints(ArrayList<Double> point) {
        this.points.clear();
        this.points.addAll(point);
    }

    public void addPoint(ArrayList<RectangleLabel> point) {
        this.bufferBox.clear();
        this.bufferBox.addAll(point);
    }

    public void addPoint(RectangleLabel point) {
        this.bufferBox.add(point);
    }


    public void setColor(Color color) {
        this.color = color;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
          if (points.size() < 2 && type != BufferType.DOT) return;

        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(this.width));

        List<Point2D> transformed = new ArrayList<>();
        int k = 0;
        for (Double p : points) {
            transformed.add(transform.transform(new Point2D.Double(k, p), null));
            k++;
        }


        switch (type) {
            case LINE:
                drawLine(g2d, transformed);
                break;
            case HISTOGRAM:
                drawHistogram(g2d, transformed, height);
                break;
            case ARROW:
                drawArrows(g2d, transformed);
                break;
            case DOT:
                drawDots(g2d, transformed);
                break;
            case BOX:
                drawBoxes(g2d, transform, width, height, false);
                break;
            case BOX_OUTLINE:
                drawBoxes(g2d, transform, width, height, true);
                break;
            case TEXT:
                drawText(g2d, transformed);
                break;
        }
    }

    private void drawLine(Graphics2D g2d, List<Point2D> points) {
        for (int i = 1; i < points.size(); i++) {
            Point2D p1 = points.get(i - 1);
            Point2D p2 = points.get(i);
            g2d.draw(new Line2D.Double(p1, p2));
        }
    }

    private void drawHistogram(Graphics2D g2d, List<Point2D> points, int baseY) {
        for (Point2D p : points) {
            double barHeight = baseY - p.getY();
            g2d.fill(new Rectangle2D.Double(
                    p.getX() - width / 2,
                    p.getY(),
                    width,
                    barHeight
            ));
        }
    }

    private void drawArrows(Graphics2D g2d, List<Point2D> points) {
        // Simple arrow implementation
        for (Point2D p : points) {
            Path2D arrow = new Path2D.Double();
            arrow.moveTo(p.getX(), p.getY());
            arrow.lineTo(p.getX() - arrowSize, p.getY() + arrowSize);
            arrow.lineTo(p.getX() + arrowSize, p.getY() + arrowSize);
            arrow.closePath();
            g2d.fill(arrow);
        }
    }

    private void drawDots(Graphics2D g2d, List<Point2D> points) {
        double radius = width * 1.5;
        for (Point2D p : points) {
            g2d.fill(new Ellipse2D.Double(
                    p.getX() - radius / 2,
                    p.getY() - radius / 2,
                    radius, radius
            ));
        }
    }

    private void drawBoxes(Graphics2D g2d, AffineTransform transform, int width, int height, boolean b) {
        for (RectangleLabel rectangleLabel : bufferBox) {
            rectangleLabel.draw(g2d, transform, width, height);
        }
    }

    private void drawText(Graphics2D g2d, List<Point2D> points) {
        // Simple text drawing (would need labels in real implementation)
        for (int i = 0; i < points.size(); i++) {
            Point2D p = points.get(i);
            g2d.drawString(String.valueOf(i), (float) p.getX(), (float) p.getY());
        }
    }

    @Override
    protected boolean contains(Point2D point) {

        return false;
    }

    @Override
    protected void move(double dx, double dy) {

    }

}