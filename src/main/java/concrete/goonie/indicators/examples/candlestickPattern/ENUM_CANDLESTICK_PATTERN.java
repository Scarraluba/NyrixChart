package concrete.goonie.chart.indicators.examples.candlestickPattern;

public enum ENUM_CANDLESTICK_PATTERN {
    PATTERN_IS_NONE(0, "No pattern"),
    PATTERN_IS_BULLCANDLE(1, "Bull Candle - Indicates bullish movement"),
    PATTERN_IS_DOJI(2, "Doji - Indecision in the market"),
    PATTERN_IS_DRAGONFLY(3, "Dragonfly Doji - Bullish reversal signal"),
    PATTERN_IS_GRAVESTONE(4, "Gravestone Doji - Bearish reversal signal"),
    PATTERN_IS_SPINNINGTOPBULLISH(5, "Spinning Top Bullish - Neutral to weak bullish trend"),
    PATTERN_IS_SPINNINGTOPBEARISH(6, "Spinning Top Bearish - Neutral to weak bearish trend"),
    PATTERN_IS_MARUBOZUUP(7, "Marubozu Up - Strong bullish momentum"),
    PATTERN_IS_MARUBOZUDOWN(8, "Marubozu Down - Strong bearish momentum"),
    PATTERN_IS_HAMMER(9, "Hammer - Bullish reversal pattern"),
    PATTERN_IS_HANGINMAN(10, "Hanging Man - Bearish reversal pattern"),
    PATTERN_IS_INVERTEDHAMMER(11, "Inverted Hammer - Bullish reversal signal"),
    PATTERN_IS_SHOOTINGSTAR(12, "Shooting Star - Bearish reversal signal"),
    PATTERN_IS_BULLISHENGULFING(13, "Bullish Engulfing - Strong bullish reversal"),
    PATTERN_IS_BEARISHENGULFING(14, "Bearish Engulfing - Strong bearish reversal"),
    PATTERN_IS_TWEEZERTOP(15, "Tweezer Top - Bearish reversal"),
    PATTERN_IS_TWEEZERBOTTOM(16, "Tweezer Bottom - Bullish reversal"),
    PATTERN_IS_THREEWHITESOLDIERS(17, "Three White Soldiers - Bullish continuation"),
    PATTERN_IS_THREECROWS(18, "Three Black Crows - Bearish continuation"),
    PATTERN_IS_THREEINSIDEUP(19, "Three Inside Up - Bullish reversal"),
    PATTERN_IS_THREEINSIDEDOWN(20, "Three Inside Down - Bearish reversal"),
    PATTERN_IS_MORNINGSTAR(21, "Morning Star - Bullish reversal"),
    PATTERN_IS_EVENINGSTAR(22, "Evening Star - Bearish reversal"),
    PATTERN_IS_BEARCANDLE(23, "Bear Candle - Indicates bearish movement");

    private final int code;
    private final String description;

    ENUM_CANDLESTICK_PATTERN(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
