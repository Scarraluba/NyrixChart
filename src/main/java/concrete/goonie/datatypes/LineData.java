package concrete.goonie.datatypes;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class LineData {

    private final List<Point2D> points;

    private Color color = Color.red;
    private int width = 1;

    public LineData() {
        points = new ArrayList<>();
    }

    public void addPoint(Point2D point) {
        points.add(point);
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void clear() {
        points.clear();
    }

    public int size(){
        return points.size();
    }
}
