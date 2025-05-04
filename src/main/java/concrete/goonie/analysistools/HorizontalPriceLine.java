package concrete.goonie.analysistools;

import concrete.goonie.ChartConfig;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class HorizontalPriceLine extends HorizontalLine {
    Font interFont = new Font("Arial", Font.BOLD, (int) 12);

    public HorizontalPriceLine(ChartConfig config, double price1) {
        super(config, price1);
        setColor(config.getLineColor());

    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        Point2D transformedStart = transform.transform(new Point2D.Double(0, line.getY1()), null);
        int xx = width - config.getyPad();
        Point2D transformedEnd = transform.transform(new Point2D.Double(xx, line.getY2()), null);

        // Create a new transformed line for drawing
        Line2D.Double transformedLine = new Line2D.Double(
                new Point2D.Double(0, transformedStart.getY()),
                new Point2D.Double(xx, transformedEnd.getY())
        );

        g2d.setColor(color);

        if (isDotted) {
            float[] dashPattern = {5f, 5f};  // Dash pattern (dotted)
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dashPattern, 0f));
        } else {
            g2d.setStroke(new BasicStroke(strokeWidth));  // Solid line
        }

        // Draw the price label at the right edge of the chart (width + config.getyPad())
        Point2D transformed = transform.transform(new Point2D.Double(xx, line.getY2()), null);
        String priceLabel = String.format("%.2f", getPrice1());

        g2d.draw(transformedLine);

        int labelX = width - config.getyPad();
        frameRect.setRoundRect(labelX, transformed.getY() - 10, config.getyPad()-1, 20, radius, radius);
        g2d.draw(frameRect);
        g2d.setColor(shapeFill);
        g2d.fill(frameRect);
        g2d.setColor(color);
        g2d.setFont(interFont);
        int l = g2d.getFontMetrics().stringWidth(priceLabel);
        g2d.drawString(priceLabel, labelX + 10, (int) transformed.getY() + 5);


    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        shapeFill = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
    }
    // Note: The drawHandles method is inherited from HorizontalLine
    // Note: All other methods are inherited from HorizontalLine
}