package concrete.goonie.indicators;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;
import concrete.goonie.data.BarSeries;
import concrete.goonie.data.HistoricalData;
import concrete.goonie.enums.ENUM_TIMEFRAME;
import concrete.goonie.symbol.Symbol;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Indicators extends Renderer {
    private final List<Indicator> indicators = new ArrayList<>();

    public Indicators(ChartConfig config) {
        super(config);

    }

    // Basic indicator management
    public void add(Indicator indicator) {
        if (indicator != null && !indicators.contains(indicator)) {
            indicators.add(indicator);
        }
    }

    public void remove(Indicator indicator) {
        indicators.remove(indicator);
    }

    public void clear() {
        indicators.clear();
    }

    public List<Indicator> getAll() {
        return new ArrayList<>(indicators);
    }

    // Core calculation method
    public void calculate(HistoricalData data) {
        if (data == null) return;

        for (Indicator indicator : indicators) {
            Symbol symbol = indicator.getSymbol();
            ENUM_TIMEFRAME timeframe = indicator.getTimeframe();
            BarSeries bars = data.getBarSeries(symbol, timeframe);

            if (bars == null || bars.size()==0) continue;

            indicator.calculateIndicator(
                    bars.size(),
                    bars.size() - 1,  // Always recalculate all bars
                    bars.copyDateTime(),
                    bars.copyOpen(),
                    bars.copyHigh(),
                    bars.copyLow(),
                    bars.copyClose(),
                    bars.copyVolume(),
                    bars.copySpread()
            );
        }
    }

    // Rendering
    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        for (Indicator indicator : indicators) {
            indicator.draw(g2d, transform, width, height);
        }
    }

    @Override
    protected boolean contains(Point2D point) {
        for (Indicator indicator : indicators) {
            if (indicator.contains(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void move(double dx, double dy) {
        for (Indicator indicator : indicators) {
            indicator.move(dx, dy);
        }
    }
}