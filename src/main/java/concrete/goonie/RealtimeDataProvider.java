package concrete.goonie;

import concrete.goonie.data.BarSeries;
import concrete.goonie.symbol.Symbol;

public interface RealtimeDataProvider {
    void subscribe(Symbol symbol, RealtimeDataListener listener);
    void unsubscribe(Symbol symbol);
    
    interface RealtimeDataListener {
        void onNewTick(Symbol symbol, double price, long timestamp);
        void onNewBar(Symbol symbol, BarSeries barSeries);
    }
}