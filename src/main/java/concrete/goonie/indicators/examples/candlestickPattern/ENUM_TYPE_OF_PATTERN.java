package concrete.goonie.chart.indicators.examples.candlestickPattern;

public enum ENUM_TYPE_OF_PATTERN {
    UNCERTAIN(0),    // Uncertain market direction
    BULLISH(1),      // Bullish pattern
    BEARISH(2);      // Bearish pattern

    private final int value;

    // Constructor for enum
    ENUM_TYPE_OF_PATTERN(int value) {
        this.value = value;
    }

    // Getter for value
    public int getValue() {
        return value;
    }

    // Static method to get the type of pattern based on value
    public static ENUM_TYPE_OF_PATTERN fromValue(int value) {
        switch (value) {
            case 1:
                return BULLISH;
            case 2:
                return BEARISH;
            default:
                return UNCERTAIN;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case BULLISH:
                return "Bullish";
            case BEARISH:
                return "Bearish";
            default:
                return "Uncertain";
        }
    }
}
