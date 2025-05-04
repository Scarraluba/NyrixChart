package concrete.goonie.indicators.examples;

import concrete.goonie.IndicatorListener;
import concrete.goonie.ChartConfig;
import concrete.goonie.analysistools.RectangleLabel;
import concrete.goonie.indicators.Indicator;
import concrete.goonie.chart.indicators.examples.candlestickPattern.CandlestickPattern;
import concrete.goonie.chart.indicators.examples.candlestickPattern.ENUM_CANDLESTICK_PATTERN;
import concrete.goonie.chart.indicators.examples.candlestickPattern.ENUM_TYPE_OF_PATTERN;
import concrete.goonie.enums.ENUM_TIMEFRAME;
import concrete.goonie.symbol.Symbol;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.time.LocalDateTime;

import static concrete.goonie.chart.indicators.examples.candlestickPattern.ENUM_CANDLESTICK_PATTERN.*;

public class CandlestickDetector extends Indicator {

    private CandlestickPattern candlestickPattern = new CandlestickPattern();
    private RectangleLabel rectangleLabel;
    private IndicatorListener listener;

    private ENUM_CANDLESTICK_PATTERN pattern = PATTERN_IS_NONE;
    private ENUM_TYPE_OF_PATTERN type = ENUM_TYPE_OF_PATTERN.UNCERTAIN;

    public CandlestickDetector(Symbol symbol, ChartConfig config) {
        super(symbol, config);
        rectangleLabel = new RectangleLabel(config, 0, null, 0, null, timeframe);
        rectangleLabel.setRadius(0);
        rectangleLabel.setColor(new Color(0x0FFFFFF, true));
    }

    @Override
    public void setInput(String key, Object value) {

    }

    @Override
    public void initializeBuffers(int size) {

    }

    @Override
    protected void calculateIndicator(int rates_total, int prev_calculated, LocalDateTime[] time, double[] open, double[] high, double[] low, double[] close, long[] volume, long[] spread) {

        detectAndPrintPattern(getSymbol(), timeframe, rates_total - 1, time, open, high, low, close);

        //System.out.println(pattern);
    }

    public void detectAndPrintPattern(Symbol Instrument, ENUM_TIMEFRAME TimeFrame,
                                      int shift, LocalDateTime[] time, double[] open, double[] high,
                                      double[] low, double[] close) {
        pattern = identifyPattern(Instrument, TimeFrame, shift);
        type = classifyPattern(pattern, shift, time, open, high, low, close);

        if (pattern != PATTERN_IS_NONE && type != ENUM_TYPE_OF_PATTERN.UNCERTAIN) {
            listener.onPatternDetected(pattern, rectangleLabel);
        }

    }

    private ENUM_CANDLESTICK_PATTERN identifyPattern(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        if (candlestickPattern.IsDojiNeutral(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_DOJI;
        } else if (candlestickPattern.IsDojiDragonfly(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_DRAGONFLY;
        } else if (candlestickPattern.IsDojiGravestone(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_GRAVESTONE;
        } else if (candlestickPattern.IsMarubozuUp(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_MARUBOZUUP;
        } else if (candlestickPattern.IsMarubozuDown(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_MARUBOZUDOWN;
        } else if (candlestickPattern.IsHammer(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_HAMMER;
        } else if (candlestickPattern.IsHangingMan(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_HANGINMAN;
        } else if (candlestickPattern.IsInvertedHammer(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_INVERTEDHAMMER;
        } else if (candlestickPattern.IsShootingStar(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_SHOOTINGSTAR;
        } else if (candlestickPattern.IsBullishEngulfing(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_BULLISHENGULFING;
        } else if (candlestickPattern.IsBearishEngulfing(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_BEARISHENGULFING;
        } else if (candlestickPattern.IsInsideBar(Instrument, TimeFrame, Shift)) {
            return PATTERN_IS_TWEEZERBOTTOM; // example use case
        } else {
            return PATTERN_IS_NONE;
        }
    }

    private ENUM_TYPE_OF_PATTERN classifyPattern(ENUM_CANDLESTICK_PATTERN pattern,
                                                 int shift, LocalDateTime[] time, double[] open, double[] high,
                                                 double[] low, double[] close) {
        int range;
        switch (pattern) {
            // === Bullish Patterns ===
            case PATTERN_IS_BULLCANDLE:
            case PATTERN_IS_MARUBOZUUP:
            case PATTERN_IS_HAMMER:
            case PATTERN_IS_DRAGONFLY:
                range = 1;
                //buffer.setPatternRange(range);
                break;

            case PATTERN_IS_BULLISHENGULFING:
            case PATTERN_IS_TWEEZERBOTTOM:
                range = 2;
                // buffer.setPatternRange(range);
                break;

            case PATTERN_IS_MORNINGSTAR:
            case PATTERN_IS_THREEINSIDEUP:
            case PATTERN_IS_THREEWHITESOLDIERS:
                range = 3;
                //  buffer.setPatternRange(range);
                break;

            // === Bearish Patterns ===
            case PATTERN_IS_BEARCANDLE:
            case PATTERN_IS_MARUBOZUDOWN:
            case PATTERN_IS_HANGINMAN:
            case PATTERN_IS_GRAVESTONE:
            case PATTERN_IS_SHOOTINGSTAR:
                range = 1;
                //  buffer.setPatternRange(range);
                break;

            case PATTERN_IS_BEARISHENGULFING:
            case PATTERN_IS_TWEEZERTOP:
                range = 2;
                //   buffer.setPatternRange(range);
                break;

            case PATTERN_IS_THREEINSIDEDOWN:
            case PATTERN_IS_THREECROWS:
                range = 3;
                //   buffer.setPatternRange(range);
                break;

            default:
                range = 0;
                //  buffer.setPatternRange(range);
                break;
        }

// === Set minLow, maxHigh, startTime, endTime dynamically ===
        if (range > 0) {
            double minLow = low[shift];
            double maxHigh = high[shift];
            for (int i = shift - range + 1; i <= shift; i++) {
                if (low[i] < minLow) minLow = low[i];
                if (high[i] > maxHigh) maxHigh = high[i];
            }
            rectangleLabel.set(minLow, time[shift - range + 1], maxHigh, time[shift]);
            rectangleLabel.setText(String.valueOf(pattern));
        }
// === Return pattern type ===
        switch (pattern) {
            case PATTERN_IS_BULLCANDLE:
            case PATTERN_IS_MARUBOZUUP:
            case PATTERN_IS_HAMMER:
            case PATTERN_IS_DRAGONFLY:
            case PATTERN_IS_BULLISHENGULFING:
            case PATTERN_IS_TWEEZERBOTTOM:
            case PATTERN_IS_MORNINGSTAR:
            case PATTERN_IS_THREEINSIDEUP:
            case PATTERN_IS_THREEWHITESOLDIERS:
                return ENUM_TYPE_OF_PATTERN.BULLISH;

            case PATTERN_IS_BEARCANDLE:
            case PATTERN_IS_MARUBOZUDOWN:
            case PATTERN_IS_HANGINMAN:
            case PATTERN_IS_GRAVESTONE:
            case PATTERN_IS_SHOOTINGSTAR:
            case PATTERN_IS_BEARISHENGULFING:
            case PATTERN_IS_TWEEZERTOP:
            case PATTERN_IS_THREEINSIDEDOWN:
            case PATTERN_IS_THREECROWS:
                return ENUM_TYPE_OF_PATTERN.BEARISH;

            default:
                return ENUM_TYPE_OF_PATTERN.UNCERTAIN;
        }


    }


    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        rectangleLabel.draw(g2d, transform, width, height);
    }


    public void setListener(IndicatorListener listener) {
        this.listener = listener;
    }
}
