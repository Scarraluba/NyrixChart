package concrete.goonie.symbols;


import concrete.goonie.enums.ENUM_TIMEFRAME;
import concrete.goonie.symbol.Symbol;

public class Boom1000Index extends Symbol {
    public Boom1000Index(ENUM_TIMEFRAME periodFrames) {
        super(periodFrames);

        setBid(20900.06650);
        setBidHigh(21027.43550);
        setBidLow(20504.89550);
        setAsk(20902.01850);
        setAskHigh(21029.38850);
        setAskLow(20506.84350);
        setPoint(0.00010);
        setTradeTickValue(0.00010);
        setTradeTickValueProfit(0.00010);
        setTradeTickValueLoss(0.00010);
        setTradeTickSize(0.00010);
        setTradeContractSize(1.00000);
        setVolumeMin(0.20000);
        setVolumeMax(50.00000);
        setVolumeStep(0.01000);
        setVolumeLimit(120.00000);
        setSwapLong(-20.00000);
        setSwapShort(-7.00000);
        setSwapSunday(1.00000);
        setSwapMonday(1.00000);
        setSwapTuesday(1.00000);
        setSwapWednesday(1.00000);
        setSwapThursday(1.00000);
        setSwapFriday(1.00000);
        setSwapSaturday(1.00000);
        setSessionOpen(20689.49200);
        setSessionClose(20689.51000);
        setPriceChange(1.02190);

// Integer setters
        setDigits(4);
        setSpread(19520);
        setTradeStopsLevel(26160);
        setExpirationMode(15);
        setFillingMode(1);
        setOrderMode(127);

// String setters
        setDescription("On average 1 spike occurs in the price series every 1000 ticks");
        setSymbolName("Boom 1000 Index");
        setPath("Crash Boom Indices/Boom 1000 Index");
    }
}
