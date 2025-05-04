package concrete.goonie;

import concrete.goonie.analysistools.HorizontalPriceLine;
import concrete.goonie.core.ChartMouseHandler;
import concrete.goonie.core.Renderer;
import concrete.goonie.core.datarenderers.CandlestickRenderer;
import concrete.goonie.core.datarenderers.HLRenderer;
import concrete.goonie.core.datarenderers.LineRenderer;
import concrete.goonie.datatypes.LineData;
import concrete.goonie.indicators.Indicator;
import concrete.goonie.indicators.Indicators;
import concrete.goonie.data.BarSeries;
import concrete.goonie.data.HistoricalData;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class DataRenderer extends Renderer {

    private final ArrayList<Renderer> renderers;
    private final CandlestickRenderer candlestickRenderer;
    private final HLRenderer hlcRenderer;
    private final Indicators indicators;
    private final LineRenderer lineRenderer;
    private final LineData lineData;
    private final HorizontalPriceLine horizontalLine;
    private final ChartMouseHandler mouseHandler;

    private boolean isLineChart = false;

    public DataRenderer(ChartConfig config, ChartMouseHandler mouseHandler) {
        super(config);
        this.mouseHandler = mouseHandler;
        candlestickRenderer = new CandlestickRenderer(config);
        hlcRenderer = new HLRenderer(config);
        lineRenderer = new LineRenderer(config);
        horizontalLine = new HorizontalPriceLine(config, 0);

        renderers = new ArrayList<>();
        renderers.add(candlestickRenderer);
        renderers.add(lineRenderer);
       // renderers.add(hlcRenderer);
        lineData = new LineData();

        indicators = new Indicators(config);
        renderers.add(horizontalLine);
        renderers.add(indicators);
    }

    @Override
    protected boolean contains(Point2D point) {
        return false;
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        for (Renderer element : renderers) {
            if (isLineChart) {
                if (!(element instanceof CandlestickRenderer)) {
                    element.draw(g2d, transform, width, height);
                }
            } else {
                if (!(element instanceof LineRenderer)) {
                    element.draw(g2d, transform, width, height);
                }
            }
        }


    }

    @Override
    protected void move(double dx, double dy) {

    }

    public boolean isLineChart() {
        return isLineChart;
    }

    public void setLineChart(boolean lineChart) {
        isLineChart = lineChart;
    }

    public void setCandleData(BarSeries candleData) {
        candlestickRenderer.setCandleData(candleData);
        hlcRenderer.setCandleData(candleData);
        double[] copied = candleData.copyClose();
        lineData.clear();

        for (int i = 0; i < copied.length; i++) {
            lineData.addPoint(new Point2D.Double(i, copied[i]));
        }

        lineRenderer.addLineData(lineData);
        mouseHandler.translateToLastCandle(candleData.getLastIndex(), candleData.getLastClose());
        horizontalLine.setPrice1(candleData.getLastClose());

        indicators.calculate(HistoricalData.getInstance());
    }

    public void add(Indicator indicator) {
        if (indicator != null) {
            indicators.add(indicator);
        }
    }

}
