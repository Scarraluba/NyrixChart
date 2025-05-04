package concrete.goonie;

import concrete.goonie.data.Bar;
import concrete.goonie.data.BarSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CSVReader {

    public BarSeries readCandlesFromCSV(String fileName) {
        BarSeries candleData = new BarSeries();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Load the file from resources
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            // Skip the header if it exists
            br.readLine(); // Assuming the first line is the header

            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = line.split("\t");

                    // Parse date and time
                    LocalDate date = LocalDate.parse(values[0], dateFormatter);
                    LocalTime time = (values.length > 8) ? LocalTime.parse(values[1], timeFormatter) : LocalTime.of(0, 0, 0);
                    LocalDateTime dateTime = LocalDateTime.of(date, time);

                    // Parse other fields
                    double open = Double.parseDouble(values[values.length > 8 ? 2 : 1]);
                    double high = Double.parseDouble(values[values.length > 8 ? 3 : 2]);
                    double low = Double.parseDouble(values[values.length > 8 ? 4 : 3]);
                    double close = Double.parseDouble(values[values.length > 8 ? 5 : 4]);
                    int tickVol = Integer.parseInt(values[values.length > 8 ? 6 : 5]);
                    int volume = Integer.parseInt(values[values.length > 8 ? 7 : 6]);
                    int spread = Integer.parseInt(values[values.length > 8 ? 8 : 7]);

                    // Create a Candle object and add it to the list
                    Bar candle = new Bar(dateTime, open, high, low, close, tickVol, volume, spread);
                    candle.setDateTime(dateTime);
                    candleData.addCandle(candle);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.err.println("LOADED");
        return candleData;
    }

    public int getMinValueIndex(BarSeries candleData, String field) {
        // Initialize variables to track the min value and index
        double minValue = Double.MAX_VALUE;
        int minIndex = -1;

        // Loop through the bars and find the min value and its index
        for (int i = 0; i < candleData.getBars().size(); i++) {
            Bar bar = candleData.getBar(i);
            double value;

            switch (field.toLowerCase()) {
                case "open": value = bar.getOpen(); break;
                case "high": value = bar.getHigh(); break;
                case "low": value = bar.getLow(); break;
                case "close": value = bar.getClose(); break;
                default: throw new IllegalArgumentException("Unknown field: " + field);
            }

            if (value < minValue) {
                minValue = value;
                minIndex = i; // Store the index of the min value
            }
        }

        return minIndex; // Return the index of the minimum value
    }
    public int getMaxValueIndex(BarSeries candleData, String field) {
        // Initialize variables to track the max value and index
        double maxValue = Double.MIN_VALUE;
        int maxIndex = -1;

        // Loop through the bars and find the max value and its index
        for (int i = 0; i < candleData.getBars().size(); i++) {
            Bar bar = candleData.getBar(i);
            double value;

            switch (field.toLowerCase()) {
                case "open": value = bar.getOpen(); break;
                case "high": value = bar.getHigh(); break;
                case "low": value = bar.getLow(); break;
                case "close": value = bar.getClose(); break;
                default: throw new IllegalArgumentException("Unknown field: " + field);
            }

            if (value > maxValue) {
                maxValue = value;
                maxIndex = i; // Store the index of the max value
            }
        }

        return maxIndex; // Return the index of the maximum value
    }
    public double getMinValue(BarSeries candleData, String field) {
        // Implement your logic to calculate the min value based on the field
        // e.g., min close value
        return candleData.getBars().stream()
                .mapToDouble(bar -> {
                    switch (field.toLowerCase()) {
                        case "open": return bar.getOpen();
                        case "high": return bar.getHigh();
                        case "low": return bar.getLow();
                        case "close": return bar.getClose();
                        default: throw new IllegalArgumentException("Unknown field: " + field);
                    }
                })
                .min()
                .orElse(Double.NaN); // Return NaN if no bars are present
    }
    public double getMaxValue(BarSeries candleData, String field) {
        // Implement your logic to calculate the max value based on the field
        return candleData.getBars().stream()
                .mapToDouble(bar -> {
                    switch (field.toLowerCase()) {
                        case "open": return bar.getOpen();
                        case "high": return bar.getHigh();
                        case "low": return bar.getLow();
                        case "close": return bar.getClose();
                        default: throw new IllegalArgumentException("Unknown field: " + field);
                    }
                })
                .max()
                .orElse(Double.NaN); // Return NaN if no bars are present
    }
    public BarSeries readNormalizedCandlesFromCSV(String fileName) {
        BarSeries candleData = readCandlesFromCSV(fileName); // Load raw data

        double minOpen = getMinValue(candleData, "open");
        double maxOpen = getMaxValue(candleData, "open");

        double minHigh = getMinValue(candleData, "high");
        double maxHigh = getMaxValue(candleData, "high");

        double minLow = getMinValue(candleData, "low");
        double maxLow = getMaxValue(candleData, "low");

        double minClose = getMinValue(candleData, "close");
        double maxClose = getMaxValue(candleData, "close");

        BarSeries normalizedData = new BarSeries();

        for (Bar bar : candleData.getBars()) {
            double normalizedOpen = (bar.getOpen() - minOpen) / (maxOpen - minOpen);
            double normalizedHigh = (bar.getHigh() - minHigh) / (maxHigh - minHigh);
            double normalizedLow = (bar.getLow() - minLow) / (maxLow - minLow);
            double normalizedClose = (bar.getClose() - minClose) / (maxClose - minClose);

            Bar normalizedBar = new Bar(bar.getDateTime(), normalizedOpen, normalizedHigh, normalizedLow, normalizedClose,
                    (int) bar.getTickVol(), (int)bar.getVolume(),(int) bar.getSpread());
            normalizedData.addCandle(normalizedBar);
        }

        return normalizedData;
    }

}
