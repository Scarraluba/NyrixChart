package concrete.goonie;

import concrete.goonie.core.ChartMouseHandler;
import concrete.goonie.core.axis.XAxis;
import concrete.goonie.core.axis.YAxis;
import concrete.goonie.data.BarSeries;
import concrete.goonie.enums.ENUM_TIMEFRAME;
import concrete.goonie.indicators.Indicator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

public class Chart extends JPanel {

    private final ChartConfig config;
    private final ChartMouseHandler chartMouseHandler;
    private ENUM_TIMEFRAME timeframe = ENUM_TIMEFRAME.PERIOD_H1;
    private final DataRenderer candleRenderer;
    private final YAxis yAxis;
    private final XAxis xAxis;

    public Chart(ChartConfig config) {
        this.config = config;
        this.chartMouseHandler = new ChartMouseHandler(this);
        this.xAxis = new XAxis(timeframe, config);
        this.yAxis = new YAxis(config);
        this.candleRenderer = new DataRenderer(config, chartMouseHandler);
        init();
    }

    private void init() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension panelSize = getSize();
                chartMouseHandler.setChartSize(panelSize.width, panelSize.height);
            }
        });
        setBackground(config.getBackgroundColor());


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get the current transform once
        AffineTransform chartTransform = chartMouseHandler.getTransform();
        // Restore and draw axes
        g2d.setTransform(new AffineTransform());
        yAxis.draw(g2d, chartMouseHandler.getYAxisTransform(), getWidth(), getHeight());
        xAxis.draw(g2d, chartMouseHandler.getXAxisTransform(), getWidth(), getHeight());

        // Draw candles with the proper transform
        candleRenderer.draw(g2d, chartTransform, getWidth(), getHeight());

    }

    public void setCandleData(BarSeries candleData) {
        candleRenderer.setCandleData(candleData);
        repaint();
    }

    public void addIndicator(Indicator indicator) {
        candleRenderer.add(indicator);
        repaint();
    }

    public void setLineChart(boolean lineChart) {
        candleRenderer.setLineChart(lineChart);
        repaint();
    }

    public void setTimeframe(ENUM_TIMEFRAME timeframe) {
        this.timeframe = timeframe;
        xAxis.setTimeframe(timeframe);
        repaint();
    }

    public void setAutoScaleY(boolean autoScaleY) {
        chartMouseHandler.setAutoScaleY(autoScaleY);
        repaint();
    }

    public ChartConfig getConfig() {
        return config;
    }

}
