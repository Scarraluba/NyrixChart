package concrete.goonie;

import concrete.goonie.analysistools.RectangleLabel;
import concrete.goonie.chart.indicators.examples.candlestickPattern.ENUM_CANDLESTICK_PATTERN;

public interface IndicatorListener {

    void onPatternDetected(ENUM_CANDLESTICK_PATTERN pattern, RectangleLabel rectangleLabel);
}
