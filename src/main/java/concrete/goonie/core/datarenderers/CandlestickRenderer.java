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

public class CandlestickRenderer extends Renderer {

    private BarSeries candleData;
    private ArrayList<Rectangle2D> bars;
    private Rectangle2D body;
    private int hoveredBarIndex = -1;
    private Double lastMiddleY = null;
    protected boolean isHovered;

    public CandlestickRenderer(ChartConfig config) {
        super(config);
        bars = new ArrayList<>();
        candleData = new BarSeries();
    }


    @Override
    protected boolean contains(Point2D point) {
        for (int i = 0; i < bars.size(); i++) {
            Rectangle2D rectangle2D = bars.get(i);
            if (rectangle2D.contains(point)) {
                isHovered = true;
                hoveredBarIndex = i;
                //  Chart.repaintChart();
                return true;
            }
        }
        isHovered = false;
        hoveredBarIndex = -1;
        // Chart.repaintChart();
        return false;
    }

    @Override
    protected void move(double dx, double dy) {
        // Handle movement if necessary (currently empty)
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        Double minY = null;
        Double maxY = null;


        int visibleCandleCount = (int) (width / transform.getScaleX());
        int barWidth = Math.max(1, (int) (width / (visibleCandleCount * 1.1))); // 1.2 provides some spacing


        for (int i = 0; i < candleData.getBars().size(); i++) {
            Bar candle = candleData.getBars().get(i);

            double xPos = i * 1.0;

            double open = candle.getOpen();
            double close = candle.getClose();
            double high = candle.getHigh();
            double low = candle.getLow();

            Point2D openPoint = transform.transform(new Point2D.Double(xPos, open), null);
            Point2D closePoint = transform.transform(new Point2D.Double(xPos, close), null);
            Point2D highPoint = transform.transform(new Point2D.Double(xPos, high), null);
            Point2D lowPoint = transform.transform(new Point2D.Double(xPos, low), null);

            int x = (int) openPoint.getX();
            int yOpen = (int) openPoint.getY();
            int yClose = (int) closePoint.getY();
            int yHigh = (int) highPoint.getY();
            int yLow = (int) lowPoint.getY();

            if (x + barWidth / 2 < 0 || x - barWidth / 2 > width - config.getyPad() - config.getMarginRight() / 2 || yHigh > height || yLow < 0) {
                continue;
            }

            // First visible candle sets the base range
            if (minY == null || maxY == null) {
                minY = low;
                maxY = high;
            } else {
                minY = Math.min(minY, low);
                maxY = Math.max(maxY, high);
            }

            int barHeight = Math.abs(yOpen - yClose);
            g2d.setColor(close >= open ? config.getBullishColor() : config.getBearishColor());
            g2d.drawLine(x, yHigh, x, yLow);
            g2d.fill(new Rectangle2D.Double(x - (barWidth / 2), Math.min(yOpen, yClose), barWidth, barHeight));
        }

        if (minY != null && maxY != null) {

            //mouseHandler.updateVisibleYRange(minY, maxY);
        }
    }

    public void setCandleData(BarSeries candleData) {
        this.candleData = candleData;
    }


}
