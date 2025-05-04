package concrete.goonie.indicators;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;
import concrete.goonie.enums.ENUM_TIMEFRAME;
import concrete.goonie.symbol.Symbol;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public abstract class Indicator extends Renderer {
    protected ENUM_TIMEFRAME timeframe;
    protected final ArrayList<Indicator> importedIndicators;
    protected final ArrayList<Buffer> buffers;
    protected boolean drawImportIndicator = true;
    protected final Symbol symbol;

    protected Indicator(Symbol symbol, ChartConfig config) {
        super(config);
        this.symbol = symbol;
        this.timeframe = symbol.getPeriodFrames();
        this.importedIndicators = new ArrayList<>();
        this.buffers = new ArrayList<>();
    }

    public abstract void setInput(String key, Object value);

    public abstract void initializeBuffers(int size);

    protected abstract void calculateIndicator(int rates_total, int prev_calculated, LocalDateTime[] time,
                                               double[] open, double[] high, double[] low, double[] close,
                                               long[] volume, long[] spread);

    @Override
    protected boolean contains(Point2D point) {
        return false;
    }

    public void addIndicator(Indicator indicator) {
        if (indicator != null && !importedIndicators.contains(indicator)) {
            importedIndicators.add(indicator);
        }
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {

        for (Buffer buffer : buffers) {
            buffer.draw(g2d, transform, width, height);
        }

        if (drawImportIndicator) {
            for (Indicator indicator : importedIndicators) {
                indicator.draw(g2d, transform, width, height);
            }
        }
    }

    @Override
    protected void move(double dx, double dy) {
        // No default move logic for indicators
    }

    protected void addBuffer(Buffer buffer) {
        buffers.add(buffer);
    }

    public void configure(Map<String, Object> parameters) {
        Objects.requireNonNull(parameters, "Parameters map cannot be null");
        parameters.forEach(this::setInput);
    }

    public ENUM_TIMEFRAME getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(ENUM_TIMEFRAME timeframe) {
        this.timeframe = Objects.requireNonNull(timeframe, "Timeframe cannot be null");
    }

    public Symbol getSymbol() {
        return symbol;
    }
}