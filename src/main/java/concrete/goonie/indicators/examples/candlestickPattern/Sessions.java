package concrete.goonie.chart.indicators.examples.candlestickPattern;//package concrete.goonie.chart.indicators.examples.candlestickPattern;
//
//import concrete.goonie.Mql5.properties.symbol.Symbol;
//import concrete.goonie.chart.elements.analysisTools.RectangleTool;
//import concrete.goonie.chart.elements.analysisTools.TextTool;
//import concrete.goonie.chart.elements.indicators.Buffer;
//import concrete.goonie.chart.elements.indicators.Indicator;
//
//import java.awt.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Sessions extends Indicator {
//    private String session = "London";
//    private String startTime = "";
//    private String endTime = "";
//    private String timeZone = "UTC";
//    private boolean drawBoxes = true;
//    private Map<String, Color> sessionColors = new HashMap<>();
//
//    private final Buffer activeBuffer;
//    private final Buffer highBuffer;
//    private final Buffer lowBuffer;
//    private final Buffer boxBuffer;
//    private final Buffer textBuffer;
//
//    private List<RectangleTool> sessionBoxes = new ArrayList<>();
//    private List<TextTool> sessionTexts = new ArrayList<>();
//
//    private static final Map<String, SessionTime> DEFAULT_SESSIONS = new HashMap<>();
//
//    static {
//        DEFAULT_SESSIONS.put("Sydney", new SessionTime("21:00", "06:00"));
//        DEFAULT_SESSIONS.put("Tokyo", new SessionTime("00:00", "09:00"));
//        DEFAULT_SESSIONS.put("London", new SessionTime("07:00", "16:00"));
//        DEFAULT_SESSIONS.put("New York", new SessionTime("13:00", "22:00"));
//        DEFAULT_SESSIONS.put("Asian kill zone", new SessionTime("00:00", "04:00"));
//        DEFAULT_SESSIONS.put("London open kill zone", new SessionTime("06:00", "09:00"));
//        DEFAULT_SESSIONS.put("New York kill zone", new SessionTime("11:00", "14:00"));
//        DEFAULT_SESSIONS.put("London close kill zone", new SessionTime("14:00", "16:00"));
//    }
//
//    public Sessions(int chartId, Symbol symbol) {
//        super("Sessions", chartId, symbol);
//
//        // Initialize default session colors
//        sessionColors.put("Sydney", new Color(70, 130, 180, 50));    // Steel Blue
//        sessionColors.put("Tokyo", new Color(220, 20, 60, 50));      // Crimson
//        sessionColors.put("London", new Color(50, 205, 50, 50));     // Lime Green
//        sessionColors.put("New York", new Color(255, 140, 0, 50));   // Dark Orange
//        sessionColors.put("Asian kill zone", new Color(138, 43, 226, 50)); // Blue Violet
//        sessionColors.put("London open kill zone", new Color(0, 255, 255, 50)); // Cyan
//        sessionColors.put("New York kill zone", new Color(255, 0, 255, 50)); // Magenta
//        sessionColors.put("London close kill zone", new Color(255, 215, 0, 50)); // Gold
//
//        this.activeBuffer = new Buffer("Active", Buffer.BufferType.DRAW_NONE, Color.GREEN, 0, 0);
//        this.highBuffer = new Buffer("High", Buffer.BufferType.DRAW_NONE, Color.BLUE, 0, 0);
//        this.lowBuffer = new Buffer("Low", Buffer.BufferType.DRAW_NONE, Color.RED, 0, 0);
//        this.boxBuffer = new Buffer("SessionBoxes", Buffer.BufferType.DRAW_BOX, Color.BLUE, 0, 2);
//        this.textBuffer = new Buffer("SessionTexts", Buffer.BufferType.DRAW_TEXT, Color.WHITE, 0, 0);
//
//        addBuffer(activeBuffer);
//        addBuffer(highBuffer);
//        addBuffer(lowBuffer);
//        addBuffer(boxBuffer);
//        addBuffer(textBuffer);
//    }
//
//    public void setInput(String key, Object value) {
//        switch (key) {
//            case "Session":
//                this.session = (String) value;
//                break;
//            case "StartTime":
//                this.startTime = (String) value;
//                break;
//            case "EndTime":
//                this.endTime = (String) value;
//                break;
//            case "TimeZone":
//                this.timeZone = (String) value;
//                break;
//            case "DrawBoxes":
//                this.drawBoxes = (Boolean) value;
//                break;
//            case "SessionColor":
//                if (value instanceof Color) {
//                    sessionColors.put(session, (Color) value);
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void initializeBuffers(int size) {
//        sessionBoxes.clear();
//        sessionTexts.clear();
//    }
//
//    @Override
//    protected void calculateIndicator(int rates_total, int prev_calculated,
//                                      LocalDateTime[] time, double[] open,
//                                      double[] high, double[] low, double[] close,
//                                      long[] volume, long[] spread) {
//
//        if (session.equals("Custom") && (startTime.isEmpty() || endTime.isEmpty())) {
//            throw new IllegalArgumentException("Custom session requires a start and end time");
//        }
//
//        SessionTime sessionTime = session.equals("Custom") ?
//                new SessionTime(startTime, endTime) :
//                DEFAULT_SESSIONS.get(session);
//
//        if (sessionTime == null) {
//            throw new IllegalArgumentException("Invalid session: " + session);
//        }
//
//        LocalTime start = parseTime(sessionTime.start);
//        LocalTime end = parseTime(sessionTime.end);
//
//        int[] active = new int[rates_total];
//        double[] sessionHigh = new double[rates_total];
//        double[] sessionLow = new double[rates_total];
//
//        double currentHigh = Double.MIN_VALUE;
//        double currentLow = Double.MAX_VALUE;
//
//        sessionBoxes.clear();
//        sessionTexts.clear();
//
//        RectangleTool currentBox = null;
//        double boxHigh = Double.MIN_VALUE;
//        double boxLow = Double.MAX_VALUE;
//        LocalDateTime boxStart = null;
//
//        for (int i = 0; i < rates_total; i++) {
//            LocalDateTime candleTime = time[i];
//            if (!timeZone.equals("UTC")) {
//                ZoneId zoneId = ZoneId.of(convertTimeZone(timeZone));
//                ZonedDateTime zonedTime = candleTime.atZone(zoneId);
//                candleTime = zonedTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
//            }
//
//            LocalTime candleLocalTime = candleTime.toLocalTime();
//            boolean isActive = isTimeInSession(candleLocalTime, start, end);
//
//            active[i] = isActive ? 1 : 0;
//
//            if (isActive) {
//                currentHigh = Math.max(high[i], i > 0 ? sessionHigh[i-1] : high[i]);
//                currentLow = Math.min(low[i], i > 0 && sessionLow[i-1] != 0 ? sessionLow[i-1] : low[i]);
//
//                // Update or create session box
//                if (currentBox == null) {
//                    boxStart = candleTime;
//                    boxHigh = high[i];
//                    boxLow = low[i];
//
//                    // Get color for this session
//                    Color boxColor = sessionColors.getOrDefault(session, new Color(100, 100, 255, 50));
//
//                    currentBox = new RectangleTool(boxHigh, boxStart, boxLow, candleTime, getTimeFrame());
//                    currentBox.setColor(boxColor);
//                    currentBox.setText(session);
//                 //   currentBox.setFill(true);
//                 //   currentBox.setBorderColor(new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), 200));
//
//
//
//                    // Create text label for this session
////                    TextTool text = new TextTool(
////                            session,
////                            (boxHigh + boxLow) / 2,
////                            boxStart.plusSeconds(ChronoUnit.SECONDS.between(boxStart, candleTime) / 2),
////                            Color.WHITE,
////                            new Font("Arial", Font.BOLD, 12),
////                            TextTool.CENTER,
////                            TextTool.MIDDLE,
////                            getTimeFrame()
////                    );
//                   // sessionTexts.add(text);
//                } else {
//                    boxHigh = Math.max(boxHigh, high[i]);
//                    boxLow = Math.min(boxLow, low[i]);
//                    currentBox.setPrice1(boxHigh);
//                    currentBox.setPrice2(boxLow);
//                    currentBox.setDateTime2(candleTime);
//                }
//            } else {
//                currentHigh = i > 0 ? sessionHigh[i-1] : 0;
//                currentLow = i > 0 ? sessionLow[i-1] : 0;
//
//                // Finalize current box if session ended
//                if (currentBox != null) {
//                    sessionBoxes.add(currentBox);
//                    currentBox = null;
//                }
//            }
//
//            sessionHigh[i] = currentHigh;
//            sessionLow[i] = currentLow == Double.MAX_VALUE ? 0 : currentLow;
//        }
//
//        // Add the last box if session was active at the end
//        if (currentBox != null) {
//            sessionBoxes.add(currentBox);
//        }
//
//        activeBuffer.setValues(toDoubleArray(active));
//        highBuffer.setValues(sessionHigh);
//        lowBuffer.setValues(sessionLow);
//
//        if (drawBoxes) {
//            boxBuffer.setValuesBox((ArrayList<RectangleTool>) sessionBoxes);
//            textBuffer.setValuesText(sessionTexts);
//        } else {
//            boxBuffer.setValuesBox(new ArrayList<>());
//            textBuffer.setValuesText(new ArrayList<>());
//        }
//    }
//
//    private boolean isTimeInSession(LocalTime time, LocalTime start, LocalTime end) {
//        if (start.isBefore(end)) {
//            return !time.isBefore(start) && !time.isAfter(end);
//        } else {
//            return !time.isBefore(start) || !time.isAfter(end);
//        }
//    }
//
//    private LocalTime parseTime(String timeStr) {
//        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
//    }
//
//    private String convertTimeZone(String tz) {
//        if (tz.startsWith("UTC") || tz.startsWith("GMT")) {
//            return tz.replace("UTC", "Etc/GMT").replace("GMT", "Etc/GMT");
//        }
//        return tz;
//    }
//
//    private double[] toDoubleArray(int[] array) {
//        double[] result = new double[array.length];
//        for (int i = 0; i < array.length; i++) {
//            result[i] = array[i];
//        }
//        return result;
//    }
//
//    private static class SessionTime {
//        final String start;
//        final String end;
//
//        SessionTime(String start, String end) {
//            this.start = start;
//            this.end = end;
//        }
//    }
//}