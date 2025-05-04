package concrete.goonie.indicators;

public enum ENUM_PRICE_TYPE {
    CLOSE(1),        // Close price
    OPEN(2),         // Open price
    HIGH(3),         // High price
    LOW(4),          // Low price
    MEDIAN(5),       // (High+Low)/2
    TYPICAL(6),      // (High+Low+Close)/3
    WEIGHTED(7);     // (Open+High+Low+Close)/4

    private final int value;

    ENUM_PRICE_TYPE(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
