package concrete.goonie.chart.indicators.examples.candlestickPattern;


import concrete.goonie.data.HistoricalData;
import concrete.goonie.enums.ENUM_TIMEFRAME;
import concrete.goonie.symbol.Symbol;

public class CandlestickPattern {


    HistoricalData historicalData = HistoricalData.getInstance();


    public boolean IsDojiNeutral(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return Body < Shadow * 0.05 && !IsDojiGravestone(Instrument, TimeFrame, Shift) && !IsDojiDragonfly(Instrument, TimeFrame, Shift);
    }

    public boolean IsDojiDragonfly(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return Body < Shadow * 0.05 && historicalData.iClose(Instrument, TimeFrame, Shift) > historicalData.iHigh(Instrument, TimeFrame, Shift) - Shadow * 0.05;
    }


    public boolean IsDojiGravestone(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return Body < Shadow * 0.05 && historicalData.iClose(Instrument, TimeFrame, Shift) < historicalData.iLow(Instrument, TimeFrame, Shift) + Shadow * 0.05;
    }

    public boolean IsMarubozuUp(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return historicalData.iOpen(Instrument, TimeFrame, Shift) < historicalData.iClose(Instrument, TimeFrame, Shift) &&
                historicalData.iClose(Instrument, TimeFrame, Shift) > historicalData.iHigh(Instrument, TimeFrame, Shift) - Shadow * 0.02 &&
                historicalData.iOpen(Instrument, TimeFrame, Shift) < historicalData.iLow(Instrument, TimeFrame, Shift) + Shadow * 0.02 &&
                Body > Shadow * 0.95;
    }

    public boolean IsMarubozuDown(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return historicalData.iOpen(Instrument, TimeFrame, Shift) > historicalData.iClose(Instrument, TimeFrame, Shift) &&
                historicalData.iClose(Instrument, TimeFrame, Shift) < historicalData.iHigh(Instrument, TimeFrame, Shift) + Shadow * 0.02 &&
                historicalData.iOpen(Instrument, TimeFrame, Shift) > historicalData.iLow(Instrument, TimeFrame, Shift) - Shadow * 0.02 &&
                Body > Shadow * 0.95;
    }

    public boolean IsHammer(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return historicalData.iOpen(Instrument, TimeFrame, Shift) < historicalData.iClose(Instrument, TimeFrame, Shift) &&
                historicalData.iClose(Instrument, TimeFrame, Shift) > historicalData.iHigh(Instrument, TimeFrame, Shift) - Shadow * 0.05 &&
                Body < Shadow * 0.4 && Body > Shadow * 0.1;
    }

    public boolean IsHangingMan(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return historicalData.iOpen(Instrument, TimeFrame, Shift) > historicalData.iClose(Instrument, TimeFrame, Shift) &&
                historicalData.iOpen(Instrument, TimeFrame, Shift) > historicalData.iHigh(Instrument, TimeFrame, Shift) - Shadow * 0.05 &&
                Body < Shadow * 0.4 && Body > Shadow * 0.1;
    }

    public boolean IsInvertedHammer(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return historicalData.iOpen(Instrument, TimeFrame, Shift) < historicalData.iClose(Instrument, TimeFrame, Shift) &&
                historicalData.iOpen(Instrument, TimeFrame, Shift) < historicalData.iLow(Instrument, TimeFrame, Shift) + Shadow * 0.05 &&
                Body < Shadow * 0.4 && Body > Shadow * 0.1;
    }

    public boolean IsShootingStar(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double Shadow = historicalData.iHigh(Instrument, TimeFrame, Shift) - historicalData.iLow(Instrument, TimeFrame, Shift);
        double Body = Math.abs(historicalData.iClose(Instrument, TimeFrame, Shift) - historicalData.iOpen(Instrument, TimeFrame, Shift));
        return historicalData.iOpen(Instrument, TimeFrame, Shift) > historicalData.iClose(Instrument, TimeFrame, Shift) &&
                historicalData.iClose(Instrument, TimeFrame, Shift) < historicalData.iLow(Instrument, TimeFrame, Shift) + Shadow * 0.05 &&
                Body < Shadow * 0.4 && Body > Shadow * 0.1;
    }

    public boolean IsBullishEngulfing(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        int i = Shift;
        int j = i - 1;
        double ShadowPrev = historicalData.iHigh(Instrument, TimeFrame, j) - historicalData.iLow(Instrument, TimeFrame, j);
        double BodyCurr = Math.abs(historicalData.iClose(Instrument, TimeFrame, i) - historicalData.iOpen(Instrument, TimeFrame, i));
        if (IsDojiNeutral(Instrument, TimeFrame, j)) return false;
        return historicalData.iClose(Instrument, TimeFrame, j) < historicalData.iOpen(Instrument, TimeFrame, j) &&
                historicalData.iClose(Instrument, TimeFrame, i) > historicalData.iOpen(Instrument, TimeFrame, i) &&
                historicalData.iClose(Instrument, TimeFrame, i) > historicalData.iHigh(Instrument, TimeFrame, j) &&
                BodyCurr > ShadowPrev;
    }

    public boolean IsBearishEngulfing(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        int i = Shift;
        int j = i - 1;
        double ShadowPrev = historicalData.iHigh(Instrument, TimeFrame, j) - historicalData.iLow(Instrument, TimeFrame, j);
        double BodyCurr = Math.abs(historicalData.iClose(Instrument, TimeFrame, i) - historicalData.iOpen(Instrument, TimeFrame, i));
        if (IsDojiNeutral(Instrument, TimeFrame, j)) return false;
        return historicalData.iClose(Instrument, TimeFrame, j) > historicalData.iOpen(Instrument, TimeFrame, j) &&
                historicalData.iClose(Instrument, TimeFrame, i) < historicalData.iOpen(Instrument, TimeFrame, i) &&
                historicalData.iClose(Instrument, TimeFrame, i) < historicalData.iLow(Instrument, TimeFrame, j) &&
                BodyCurr > ShadowPrev;
    }

    public boolean isBullCandle(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        return historicalData.iOpen(Instrument, TimeFrame, Shift) < historicalData.iClose(Instrument, TimeFrame, Shift);
    }

    public boolean isBearCandle(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        return historicalData.iOpen(Instrument, TimeFrame, Shift) > historicalData.iClose(Instrument, TimeFrame, Shift);
    }

    public boolean IsInsideBar(Symbol Instrument, ENUM_TIMEFRAME TimeFrame, int Shift) {
        double currentHigh = historicalData.iHigh(Instrument, TimeFrame, Shift);
        double currentLow = historicalData.iLow(Instrument, TimeFrame, Shift);
        double previousHigh = historicalData.iHigh(Instrument, TimeFrame, Shift - 1);
        double previousLow = historicalData.iLow(Instrument, TimeFrame, Shift - 1);

        return currentHigh < previousHigh && currentLow > previousLow;
    }

}
