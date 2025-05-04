package concrete.goonie.indicators.examples;

import concrete.goonie.ChartConfig;
import concrete.goonie.indicators.*;
import concrete.goonie.symbol.Symbol;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MovingAverage extends Indicator {

    private int period = 10;
    private ENUM_PRICE_TYPE priceType = ENUM_PRICE_TYPE.CLOSE;
    private ENUM_PRICE_METHOD maMethod = ENUM_PRICE_METHOD.SMOOTHED;
    private final ArrayList<Double> maBuffer = new ArrayList<>();
    private final Buffer movingAverageBuffer = new Buffer(config, BufferType.LINE, "Moving Average");

    private double[] open;
    private double[] high;
    private double[] low;
    private double[] close;

    public MovingAverage(Symbol symbol, ChartConfig config) {
        super(symbol, config);
        addBuffer(movingAverageBuffer);
        movingAverageBuffer.setColor(new Color(0x6ECF6A));
    }

    @Override
    public void setInput(String key, Object value) {
        switch (key) {
            case "period":
                this.period = (int) value;
                break;
            case "priceType":
                this.priceType = (ENUM_PRICE_TYPE) value;
                break;
            case "maMethod":
                this.maMethod = (ENUM_PRICE_METHOD) value;
                break;
            default:
                throw new IllegalArgumentException("Unknown input: " + key);
        }
    }
    @Override
    public void initializeBuffers(int size) {
        maBuffer.clear();

        for (int i = 0; i < size; i++) {
            maBuffer.add(0.0);
        }
    }
    @Override
    protected void calculateIndicator(int rates_total, int prev_calculated, LocalDateTime[] time, double[] open, double[] high, double[] low, double[] close, long[] volume, long[] spread) {

        if (rates_total < period) {
            return;
        }

        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;

         initializeBuffers(rates_total);

        for (int i = 0; i < rates_total; i++) {
            double price = getPrice(priceType, i);

            switch (maMethod) {
                case SIMPLE:
                    maBuffer.set(i, simpleMovingAverage(i, price));
                    break;
                case EXPONENTIAL:
                    maBuffer.set(i, exponentialMovingAverage(i, price));
                    break;
                case SMOOTHED:
                    maBuffer.set(i, smoothedMovingAverage(i, price));
                    break;
                case LINEARWEIGHTED:
                    maBuffer.set(i, linearWeightedMovingAverage(i, price));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown MA method");
            }
        }

        movingAverageBuffer.addPoints(maBuffer);
    }

    private double getPrice(ENUM_PRICE_TYPE priceType, int index) {
        switch (priceType) {
            case CLOSE:
                return close[index];
            case OPEN:
                return open[index];
            case HIGH:
                return high[index];
            case LOW:
                return low[index];
            case MEDIAN:
                return (high[index] + low[index]) / 2;
            case TYPICAL:
                return (high[index] + low[index] + close[index]) / 3;
            case WEIGHTED:
                return (open[index] + high[index] + low[index] + close[index]) / 4;
            default:
                throw new IllegalArgumentException("Unknown price type");
        }
    }

    // Function to calculate the Simple Moving Average (SMA)
    private double simpleMovingAverage(int index, double price) {
        double sum = 0.0;

        for (int i = index; i > index - period; i--) {
            if (i < 0) break;
            sum += getPrice(priceType, i);
        }

        return sum / period;
    }

    // Function to calculate the Exponential Moving Average (EMA)
    private double exponentialMovingAverage(int index, double price) {
        if (index == 0) return price; // Handle first element safely

        double emaPrev = (index < period) ? simpleMovingAverage(index, price) :
                (index - 1 < maBuffer.size() ? maBuffer.get(index - 1) : price);

        double smoothingConstant = 2.0 / (period + 1);
        return (price - emaPrev) * smoothingConstant + emaPrev;
    }

    // Function to calculate the Smoothed Moving Average (SMMA)
    private double smoothedMovingAverage(int index, double price) {
        if (index < period) return simpleMovingAverage(index, price);

        if (index - 1 >= maBuffer.size()) return price; // Ensure valid access

        double prevSmma = maBuffer.get(index - 1);
        return (prevSmma * (period - 1) + price) / period;
    }

    // Function to calculate the Linear Weighted Moving Average (LWMA)
    private double linearWeightedMovingAverage(int index, double price) {
        double weightedSum = 0.0;
        double weightTotal = 0.0;

        for (int i = index, weight = period; i >= 0 && weight > 0; i--, weight--) {
            weightedSum += getPrice(priceType, i) * weight;
            weightTotal += weight;
        }

        return weightTotal > 0 ? weightedSum / weightTotal : price;
    }
}
