package concrete.goonie.analysistools;


import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;
import concrete.goonie.enums.ENUM_TIMEFRAME;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Tool extends Renderer {
    protected LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);
    protected Font textFont = new Font("Arial", Font.BOLD, 10);
    protected ENUM_TIMEFRAME timeframe = ENUM_TIMEFRAME.PERIOD_H1;
    protected Color color = new Color(0x3AB6E5);

    protected boolean isHovered;
    protected Color shapeFill;
    protected boolean isDraggable;
    protected boolean isDotted;

    protected double price1;
    protected double price2;
    protected LocalDateTime dateTime1;
    protected LocalDateTime dateTime2;

    protected String text = "";
    protected double radius = 10;
    protected int strokeWidth = 1;
    protected final RoundRectangle2D.Double frameRect;

    protected final int HANDLE_SIZE = 12;

    protected Tool(ChartConfig config, double price1, LocalDateTime dateTime1, ENUM_TIMEFRAME timeframe) {
        super(config);

        this.price1 = price1;
        this.timeframe = timeframe;
        this.dateTime1 = dateTime1;
        this.frameRect = new RoundRectangle2D.Double();
        shapeFill = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
    }

    protected Tool(ChartConfig config, double price1, LocalDateTime dateTime1, double price2, LocalDateTime dateTime2, ENUM_TIMEFRAME timeframe) {
        this(config, price1, dateTime1, timeframe);
        this.price2 = price2;
        this.dateTime2 = dateTime2;
    }

    protected Tool(ChartConfig config, String text, double price1, LocalDateTime dateTime1, double price2, LocalDateTime dateTime2, ENUM_TIMEFRAME timeframe) {
        this(config, price1, dateTime1, price2, dateTime2, timeframe);
        this.text = text;
    }

    public abstract double calculateArea();

    public abstract boolean overlapsWith(Tool other);

    @Override
    protected boolean contains(Point2D point) {
        return frameRect.contains(point);
    }

    protected double timeToXCoordinate(LocalDateTime dateTime) {
        Duration duration = Duration.between(startDateTime, dateTime);
        return (double) duration.toSeconds() / timeframe.getDuration().toSeconds();
    }

    protected LocalDateTime xCoordinateToTime(double x) {
        Duration duration = timeframe.getDuration().multipliedBy((long) x);
        return startDateTime.plus(duration);
    }

    public void setColor(Color color) {
        this.color = color;
        shapeFill = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
    }

    public Color getColor() {
        return this.color;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Font getTextFont() {
        return textFont;
    }

    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }

    public ENUM_TIMEFRAME getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(ENUM_TIMEFRAME timeframe) {
        this.timeframe = timeframe;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public void setHovered(boolean hovered) {
        isHovered = hovered;
    }

    public Color getShapeFill() {
        return shapeFill;
    }

    public void setShapeFill(Color shapeFill) {
        this.shapeFill = shapeFill;
    }

    public double getPrice1() {
        return price1;
    }

    public void setPrice1(double price1) {
        this.price1 = price1;
    }

    public double getPrice2() {
        return price2;
    }

    public void setPrice2(double price2) {
        this.price2 = price2;
    }

    public LocalDateTime getDateTime1() {
        return dateTime1;
    }

    public void setDateTime1(LocalDateTime dateTime1) {
        this.dateTime1 = dateTime1;
    }

    public LocalDateTime getDateTime2() {
        return dateTime2;
    }

    public void setDateTime2(LocalDateTime dateTime2) {
        this.dateTime2 = dateTime2;
    }

    public void set(double price1, LocalDateTime dateTime1, double price2, LocalDateTime dateTime2) {
        this.price1 = price1;
        this.dateTime2 = dateTime2;
        this.dateTime1 = dateTime1;
        this.price2 = price2;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public boolean isDraggable() {
        return isDraggable;
    }

    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
    }

    public boolean isDotted() {
        return isDotted;
    }

    public void setDotted(boolean dotted) {
        isDotted = dotted;
    }

}
