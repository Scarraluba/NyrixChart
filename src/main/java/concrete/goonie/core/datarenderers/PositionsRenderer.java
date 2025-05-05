package concrete.goonie.core.datarenderers;

import concrete.goonie.ChartConfig;
import concrete.goonie.Positions;
import concrete.goonie.account.Account;
import concrete.goonie.analysistools.PositionLine;
import concrete.goonie.core.Renderer;
import concrete.goonie.trade.position.Position;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class PositionsRenderer extends Renderer {
    private ArrayList<PositionLine> lines = new ArrayList<>();
    private Positions candleData;
    private PositionLine positionLine;

    public PositionsRenderer(ChartConfig config) {
        super(config);
        candleData = Account.getInstance().getPositions();
    }

    @Override
    protected boolean contains(Point2D point) {
        for (PositionLine rectangle2D : lines) {
            if (rectangle2D.contains(point))
                return rectangle2D.contains(point);
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g2d, AffineTransform transform, int width, int height) {
        lines.clear();

        for (int i = 0; i < Account.getInstance().getPositions().getPositionsTotal(); i++) {
            Position position = candleData.getOpenPositions().get(i);

            positionLine = new PositionLine(config,position);
            positionLine.draw(g2d, transform, width, height);
            lines.add(positionLine);
//                if (Objects.equals(symbol.getSymbolInfoString(ENUM_SYMBOL_INFO_STRING.SYMBOL_NAME),
//                        position.getPositionString(ENUM_POSITION_PROPERTY_STRING.POSITION_SYMBOL))) {
//                }
        }
    }

    @Override
    protected void move(double dx, double dy) {

    }
}
