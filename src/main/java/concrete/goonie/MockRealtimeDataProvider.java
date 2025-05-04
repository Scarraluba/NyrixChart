package concrete.goonie;

import concrete.goonie.data.BarSeries;
import concrete.goonie.data.HistoricalData;
import concrete.goonie.symbol.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MockRealtimeDataProvider implements RealtimeDataProvider {
    private final Map<Symbol, RealtimeDataListener> subscribers = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Random random = new Random();
    private final HistoricalData historicalData;
    private final HistoricalData loadSymbolData;
    private BarSeries currentBarSeries;
    private Symbol currentSymbol;
    private int barIndex = 0;
    private BarSeries ba;

    public MockRealtimeDataProvider(HistoricalData historicalData, HistoricalData loadSymbolData) {
        this.historicalData = historicalData;
        this.loadSymbolData = loadSymbolData;
    }

    @Override
    public void subscribe(Symbol symbol, RealtimeDataListener listener) {
        subscribers.put(symbol, listener);
        currentSymbol = symbol;
        currentBarSeries = loadSymbolData.getBarSeries(symbol, symbol.getPeriodFrames());
        ba = new BarSeries();
        historicalData.addBarSeries(symbol, symbol.getPeriodFrames(), ba);

        // Start simulating ticks
        //    scheduler.scheduleAtFixedRate(this::simulateTick, 0, 500, TimeUnit.MILLISECONDS);

        // Start simulating new bars
        scheduler.scheduleAtFixedRate(this::simulateNewBar, 5, 300, TimeUnit.MILLISECONDS);
    }

    @Override
    public void unsubscribe(Symbol symbol) {
        subscribers.remove(symbol);
        if (subscribers.isEmpty()) {
            scheduler.shutdown();
        }
    }

    private void simulateTick() {
        if (currentBarSeries == null || currentBarSeries.size() == 0) return;

        double lastClose = currentBarSeries.getLastClose();
        // Random price change between -0.5% and +0.5%
        double priceChange = lastClose * (0.005 * (2 * random.nextDouble() - 1));
        double newPrice = lastClose + priceChange;

        RealtimeDataListener listener = subscribers.get(currentSymbol);
        if (listener != null) {
            listener.onNewTick(currentSymbol, newPrice, System.currentTimeMillis());
        }
    }

    private void simulateNewBar() {
        if (currentBarSeries == null || currentBarSeries.size() == 0) return;

        //    HistoricalData.copyBarByIndex(loadSymbolData, historicalData, barIndex);

        ba.addCandle(currentBarSeries.getBar(barIndex));

        // if (ba == null || ba.size() == 0) return;

        RealtimeDataListener listener = subscribers.get(currentSymbol);
        if (listener != null) {
            listener.onNewBar(currentSymbol, ba);
        }
        if (currentBarSeries.size() == barIndex) {
            unsubscribe(currentSymbol);
        }
        barIndex++;
    }
}