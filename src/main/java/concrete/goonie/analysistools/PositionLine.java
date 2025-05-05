package concrete.goonie.analysistools;

import concrete.goonie.ChartConfig;
import concrete.goonie.core.Renderer;
import concrete.goonie.trade.position.Position;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static concrete.goonie.enums.ENUM_POSITION_TYPE.POSITION_TYPE_BUY;

public class PositionLine extends Renderer {
    private List<HorizontalLine> lines = new ArrayList<>();

    public PositionLine(ChartConfig config,Position position) {
        super(config);
        double volume = position.getVolume();
        lines.add(new HorizontalLine(config,position.getPriceOpen(),
                new Color(0x266688), false, true, position.getType() == POSITION_TYPE_BUY.ordinal() ? "Buy " + volume : "Sell " + volume));
        lines.add(new HorizontalLine(config,position.getStopLoss(),
                Color.RED, true, true, "Stop Loss"));
        lines.add(new HorizontalLine(config,position.getTakeProfit(),
                Color.GREEN, true, true, "Take Profit"));
    }

    @Override
    public boolean contains(Point2D point) {
        for (HorizontalLine line : lines) {
            return line.contains(point);
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        for (HorizontalLine line : lines) {
            line.draw(g2d, transform, width, height);
        }
    }

    @Override
    protected void move(double dx, double dy) {

    }
}
