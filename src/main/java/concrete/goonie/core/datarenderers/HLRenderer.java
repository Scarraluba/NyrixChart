package concrete.goonie.core.datarenderers;


import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;
import concrete.goonie.data.Bar;
import concrete.goonie.data.BarSeries;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class HLRenderer extends Renderer {

    private BarSeries data;
    private ArrayList<Rectangle2D> bars;
    private int hoveredBarIndex = -1;
    protected boolean isHovered;

    public HLRenderer(ChartConfig config) {
        super(config);
        bars = new ArrayList<>();
        data = new BarSeries();
    }

    @Override
    protected boolean contains(Point2D point) {
        for (int i = 0; i < bars.size(); i++) {
            Rectangle2D rectangle2D = bars.get(i);
            if (rectangle2D.contains(point)) {
                isHovered = true;
                hoveredBarIndex = i;
                return true;
            }
        }
        isHovered = false;
        hoveredBarIndex = -1;
        return false;
    }

    @Override
    protected void move(double dx, double dy) {
        // Handle movement if necessary
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        Double minY = null;
        Double maxY = null;

        int visibleBarCount = (int) (width / transform.getScaleX());
        int barWidth = Math.max(1, (int) (width / (visibleBarCount * 1.1))); // Provides some spacing

        for (int i = 0; i < data.getBars().size(); i++) {
            Bar bar = data.getBars().get(i);
            double xPos = i * 1.0;

            double high = bar.getHigh();
            double low = bar.getLow();
            double close = bar.getClose();

            Point2D highPoint = transform.transform(new Point2D.Double(xPos, high), null);
            Point2D lowPoint = transform.transform(new Point2D.Double(xPos, low), null);
            Point2D closePoint = transform.transform(new Point2D.Double(xPos, close), null);

            int x = (int) highPoint.getX();
            int yHigh = (int) highPoint.getY();
            int yLow = (int) lowPoint.getY();


            if (x + barWidth / 2 < 0 || x - barWidth / 2 > width - config.getyPad() - config.getMarginRight() / 2 || 
                yHigh > height || yLow < 0) {
                continue;
            }

            // First visible bar sets the base range
            if (minY == null || maxY == null) {
                minY = low;
                maxY = high;
            } else {
                minY = Math.min(minY, low);
                maxY = Math.max(maxY, high);
            }

            int barHeight = Math.abs(yLow - yHigh);
            g2d.fill(new Rectangle2D.Double(x - (barWidth / 2), Math.min(yLow, yHigh) , barWidth, barHeight));
        }

        if (minY != null && maxY != null) {
            // Update visible range if needed
            // mouseHandler.updateVisibleYRange(minY, maxY);
        }
    }

    public void setCandleData(BarSeries data) {
        this.data = data;
    }
}